interface PaginationProps {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}

export function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  return (
    <div className="flex items-center justify-center gap-2 py-4">
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        className="rounded-md border border-neutral-300 px-3 py-1 text-sm disabled:opacity-50"
      >
        Anterior
      </button>
      <span className="text-sm text-neutral-600">
        Página {page + 1} de {totalPages}
      </span>
      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        className="rounded-md border border-neutral-300 px-3 py-1 text-sm disabled:opacity-50"
      >
        Próxima
      </button>
    </div>
  )
}
