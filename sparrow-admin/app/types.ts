import type { ColumnDef } from "@tanstack/react-table";

export type PageResponse<T> = {
  content: T[];
  page: {
    size: number; // current page size
    number: number; // current page number
    totalElements: number;
    totolPages: number;
  }
};

export type DataTableProps<TData, TValue> = {
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
};