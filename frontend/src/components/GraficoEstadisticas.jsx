import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "react-chartjs-2";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export default function GraficoEstadisticas({ datos = [] }) {
  // Con muchos puntos el grafico se vuelve ilegible: mostramos los 30 mas recientes
  const recientes = [...datos]
    .sort((a, b) => new Date(b.fechaMedicion) - new Date(a.fechaMedicion))
    .slice(0, 30);

  const labels = recientes.map((d) =>
    d.fechaMedicion ? new Date(d.fechaMedicion).toLocaleDateString() : `#${d.id}`
  );

  const data = {
    labels,
    datasets: [
      {
        label: "PM2.5",
        data: recientes.map((d) => d.pm25 ?? 0),
        backgroundColor: "#e74c3c",
      },
      {
        label: "PM10",
        data: recientes.map((d) => d.pm10 ?? 0),
        backgroundColor: "#3498db",
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true, text: "Concentracion de contaminantes" },
    },
  };

  return <Bar data={data} options={options} />;
}
