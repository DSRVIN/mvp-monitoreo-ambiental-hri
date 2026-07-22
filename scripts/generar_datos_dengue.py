#!/usr/bin/env python3
"""
Genera los datos agregados de dengue para el mapa de calor del sistema.

Procesa el CSV abierto de vigilancia de dengue del MINSA (2000-2024), filtra el
departamento de ICA, agrega los casos por distrito y por anio, y les asigna
coordenadas geograficas. El resultado es un JSON compacto que consume el backend
para servir el mapa de calor.

Uso:
    python generar_datos_dengue.py [ruta_csv] [ruta_salida_json]

Por defecto lee el CSV desde la carpeta de descargas y escribe el JSON en los
recursos del backend.

Fuente de datos: Plataforma Nacional de Datos Abiertos del Peru (MINSA/CDC).
"""
import csv
import json
import os
import sys
from collections import defaultdict
from datetime import date

# Coordenadas (lat, lng) por codigo UBIGEO de distrito del departamento de Ica.
# Se usa el UBIGEO como clave para evitar problemas de codificacion en los nombres.
COORDENADAS = {
    # Provincia de ICA
    "110101": (-14.0678, -75.7286, "ICA", "ICA"),
    "110102": (-14.0344, -75.7089, "LA TINGUIÑA", "ICA"),
    "110103": (-14.1167, -75.6833, "LOS AQUIJES", "ICA"),
    "110104": (-14.3667, -75.6833, "OCUCAJE", "ICA"),
    "110105": (-14.1000, -75.7500, "PACHACUTEC", "ICA"),
    "110106": (-14.0453, -75.6883, "PARCONA", "ICA"),
    "110107": (-14.0800, -75.7000, "PUEBLO NUEVO", "ICA"),
    "110108": (-13.9833, -75.6667, "SALAS", "ICA"),
    "110109": (-13.9000, -75.6333, "SAN JOSE DE LOS MOLINOS", "ICA"),
    "110110": (-14.0500, -75.7333, "SAN JUAN BAUTISTA", "ICA"),
    "110111": (-14.1667, -75.7167, "SANTIAGO", "ICA"),
    "110112": (-14.0339, -75.7519, "SUBTANJALLA", "ICA"),
    "110113": (-14.1333, -75.6500, "TATE", "ICA"),
    "110114": (-14.2833, -75.5333, "YAUCA DEL ROSARIO", "ICA"),
    # Provincia de CHINCHA
    "110201": (-13.4100, -76.1319, "CHINCHA ALTA", "CHINCHA"),
    "110202": (-13.4667, -76.1000, "ALTO LARAN", "CHINCHA"),
    "110204": (-13.4500, -76.1667, "CHINCHA BAJA", "CHINCHA"),
    "110205": (-13.5333, -76.1333, "EL CARMEN", "CHINCHA"),
    "110206": (-13.3833, -76.1500, "GROCIO PRADO", "CHINCHA"),
    "110207": (-13.4000, -76.1167, "PUEBLO NUEVO", "CHINCHA"),
    "110210": (-13.4167, -76.1500, "SUNAMPE", "CHINCHA"),
    "110211": (-13.4667, -76.1833, "TAMBO DE MORA", "CHINCHA"),
    # Provincia de PISCO
    "110501": (-13.7100, -76.2036, "PISCO", "PISCO"),
    "110502": (-13.5833, -75.6667, "HUANCANO", "PISCO"),
    "110503": (-13.7167, -75.9000, "HUMAY", "PISCO"),
    "110504": (-13.6833, -76.1167, "INDEPENDENCIA", "PISCO"),
    "110505": (-13.8600, -76.2500, "PARACAS", "PISCO"),
    "110506": (-13.7333, -76.2167, "SAN ANDRES", "PISCO"),
    "110507": (-13.6833, -76.1500, "SAN CLEMENTE", "PISCO"),
    "110508": (-13.7000, -76.1833, "TUPAC AMARU INCA", "PISCO"),
    # Provincia de NAZCA
    "110301": (-14.8294, -74.9375, "NAZCA", "NAZCA"),
    "110302": (-14.7500, -75.2000, "CHANGUILLO", "NAZCA"),
    "110303": (-14.6833, -75.1500, "EL INGENIO", "NAZCA"),
    "110304": (-15.2100, -75.1400, "MARCONA", "NAZCA"),
    "110305": (-14.8500, -74.9333, "VISTA ALEGRE", "NAZCA"),
    # Provincia de PALPA
    "110401": (-14.5333, -75.1833, "PALPA", "PALPA"),
    "110402": (-14.5000, -75.1667, "LLIPATA", "PALPA"),
    "110403": (-14.5167, -75.2167, "RIO GRANDE", "PALPA"),
    "110404": (-14.4667, -75.1500, "SANTA CRUZ", "PALPA"),
}

RUTA_CSV_DEFAULT = os.path.expanduser(
    "~/Downloads/dengue_data/datos_abiertos_vigilancia_dengue_2000_2024.csv"
)
RUTA_SALIDA_DEFAULT = os.path.join(
    os.path.dirname(__file__), "..", "backend", "src", "main", "resources", "data", "dengue-ica.json"
)


def main():
    ruta_csv = sys.argv[1] if len(sys.argv) > 1 else RUTA_CSV_DEFAULT
    ruta_salida = sys.argv[2] if len(sys.argv) > 2 else RUTA_SALIDA_DEFAULT

    if not os.path.exists(ruta_csv):
        print(f"ERROR: no se encontro el CSV en {ruta_csv}")
        sys.exit(1)

    # casos[ubigeo][anio] = numero de casos ; severidad[ubigeo] = conteo por tipo
    casos = defaultdict(lambda: defaultdict(int))
    severidad = defaultdict(lambda: defaultdict(int))
    total = 0

    # El CSV de dengue viene en codificacion latin-1 y usa ';' como separador
    with open(ruta_csv, encoding="latin-1", newline="") as f:
        lector = csv.reader(f, delimiter=";")
        cabecera = next(lector)
        # Limpia el BOM (que en latin-1 aparece como "ï»¿") y comillas de los nombres
        def limpiar(nombre):
            return nombre.strip().replace("﻿", "").replace("ï»¿", "").strip('"')
        idx = {limpiar(nombre): i for i, nombre in enumerate(cabecera)}
        i_dep, i_anio, i_ubigeo, i_diag = (
            idx["departamento"], idx["ano"], idx["ubigeo"], idx["enfermedad"],
        )
        for fila in lector:
            if len(fila) <= i_diag or fila[i_dep].strip() != "ICA":
                continue
            ubigeo = fila[i_ubigeo].strip()
            if ubigeo not in COORDENADAS:
                continue
            anio = fila[i_anio].strip()
            casos[ubigeo][anio] += 1
            severidad[ubigeo][clasificar(fila[i_diag])] += 1
            total += 1

    puntos = []
    for ubigeo, por_anio in casos.items():
        lat, lng, distrito, provincia = COORDENADAS[ubigeo]
        puntos.append({
            "ubigeo": ubigeo,
            "distrito": distrito,
            "provincia": provincia,
            "lat": lat,
            "lng": lng,
            "casos": sum(por_anio.values()),
            "porAnio": dict(sorted(por_anio.items())),
            "severidad": dict(severidad[ubigeo]),
        })
    puntos.sort(key=lambda p: p["casos"], reverse=True)

    anios = sorted({a for p in puntos for a in p["porAnio"]})
    resultado = {
        "meta": {
            "fuente": "MINSA/CDC Peru - Datos abiertos de vigilancia de dengue 2000-2024",
            "departamento": "ICA",
            "generado": date.today().isoformat(),
            "totalCasos": total,
            "distritos": len(puntos),
            "anios": anios,
        },
        "puntos": puntos,
    }

    os.makedirs(os.path.dirname(ruta_salida), exist_ok=True)
    with open(ruta_salida, "w", encoding="utf-8") as f:
        json.dump(resultado, f, ensure_ascii=False, indent=2)

    print(f"OK: {total} casos de dengue en {len(puntos)} distritos de Ica")
    print(f"Anios: {anios[0]}-{anios[-1]}")
    print(f"Archivo generado: {os.path.abspath(ruta_salida)}")


def clasificar(diagnostico):
    d = diagnostico.upper()
    if "GRAVE" in d:
        return "GRAVE"
    if "CON SIGNOS" in d:
        return "CON_ALARMA"
    return "SIN_ALARMA"


if __name__ == "__main__":
    main()
