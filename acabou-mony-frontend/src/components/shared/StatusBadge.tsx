import {
  ACCOUNT_STATUS_MAP,
  TRANSACTION_STATUS_MAP,
  TRANSACTION_TYPE_MAP,
} from "../../lib/constants"

interface StatusBadgeProps {
  type: "account" | "transaction" | "transactionType"
  value: string
}

export function StatusBadge({ type, value }: StatusBadgeProps) {
  let config: { label: string; color: string } | undefined

  if (type === "account") config = ACCOUNT_STATUS_MAP[value]
  else if (type === "transaction") config = TRANSACTION_STATUS_MAP[value]
  else if (type === "transactionType") {
    return (
      <span className="inline-flex items-center rounded-md bg-neutral-100 px-2.5 py-0.5 text-xs font-medium text-neutral-700">
        {TRANSACTION_TYPE_MAP[value] ?? value}
      </span>
    )
  }

  if (!config) return <span>{value}</span>

  return (
    <span
      className={`inline-flex items-center rounded-md px-2.5 py-0.5 text-xs font-medium ${config.color}`}
    >
      {config.label}
    </span>
  )
}
