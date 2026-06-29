import { Link } from "react-router-dom"
import { Button } from "../components/ui/button"

export function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4">
      <h1 className="text-6xl font-bold text-neutral-300">404</h1>
      <p className="text-lg text-neutral-500">Página não encontrada</p>
      <Link to="/dashboard">
        <Button>Voltar ao Dashboard</Button>
      </Link>
    </div>
  )
}
