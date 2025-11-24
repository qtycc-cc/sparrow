import type { ColumnDef } from "@tanstack/react-table";

// RFC 9457 standard
export type ProblemDetail= {
  type: URL | string;
  title?: string;
  status: number;
  detail?: string;
  instance?: URL | string;
  [key : string]: unknown;
};

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