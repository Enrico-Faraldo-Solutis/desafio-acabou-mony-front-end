export interface PageResponse<T> {
  content: T[]
  pageable: { pageNumber: number; pageSize: number }
  totalPages: number
  totalElements: number
  last: boolean
  first: boolean
  empty: boolean
}
