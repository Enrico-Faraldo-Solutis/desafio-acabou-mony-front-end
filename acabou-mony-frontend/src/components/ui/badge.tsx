import { cn } from "../../lib/utils"

interface BadgeProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: "default" | "secondary" | "destructive" | "outline"
}

export function Badge({ className, variant = "default", ...props }: BadgeProps) {
  return (
    <div
      className={cn(
        "inline-flex items-center rounded-md border px-2.5 py-0.5 text-xs font-semibold transition-colors",
        {
          "border-transparent bg-neutral-900 text-neutral-50 shadow": variant === "default",
          "border-transparent bg-neutral-100 text-neutral-900": variant === "secondary",
          "border-transparent bg-red-600 text-neutral-50 shadow": variant === "destructive",
          "text-neutral-950": variant === "outline",
        },
        className,
      )}
      {...props}
    />
  )
}
