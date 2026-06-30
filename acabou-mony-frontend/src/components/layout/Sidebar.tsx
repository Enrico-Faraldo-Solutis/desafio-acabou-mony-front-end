import { NavLink } from "react-router-dom"
import { cn } from "../../lib/utils"
import {
  LayoutDashboard,
  Building2,
  CreditCard,
  ArrowLeftRight,
  ScrollText,
  LogOut,
} from "lucide-react"
import { useAuthStore } from "../../store/auth.store"

const navItems = [
  { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { to: "/accounts", label: "Contas", icon: Building2 },
  { to: "/cards", label: "Cartões", icon: CreditCard },
  { to: "/transactions", label: "Transações", icon: ArrowLeftRight },
  { to: "/auditing", label: "Auditoria", icon: ScrollText },
]

export function Sidebar() {
  const logout = useAuthStore((s) => s.logout)

  return (
    <aside className="flex h-screen w-64 flex-col border-r border-neutral-200 bg-white">
      <div className="flex h-14 items-center border-b border-neutral-200 px-6">
        <h1 className="text-lg font-bold text-neutral-900">Acabou Mony</h1>
      </div>

      <nav className="flex-1 space-y-1 p-4">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-neutral-100 text-neutral-900"
                  : "text-neutral-500 hover:bg-neutral-50 hover:text-neutral-900",
              )
            }
          >
            <item.icon className="h-4 w-4" />
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="border-t border-neutral-200 p-4">
        <button
          onClick={logout}
          className="flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-neutral-500 hover:bg-neutral-50 hover:text-neutral-900"
        >
          <LogOut className="h-4 w-4" />
          Sair
        </button>
      </div>
    </aside>
  )
}
