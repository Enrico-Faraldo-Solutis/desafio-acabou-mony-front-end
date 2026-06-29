import { formatCurrency } from "../../lib/utils"

interface CurrencyDisplayProps {
  value: number
  className?: string
}

export function CurrencyDisplay({ value, className }: CurrencyDisplayProps) {
  const isNegative = value < 0
  return (
    <span className={`font-mono ${isNegative ? "text-red-600" : ""} ${className ?? ""}`}>
      {formatCurrency(value)}
    </span>
  )
}
