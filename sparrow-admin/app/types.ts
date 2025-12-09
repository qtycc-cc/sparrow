import type { ColumnDef, OnChangeFn, PaginationState } from "@tanstack/react-table";

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
    totalPages: number;
  }
};

export type App = {
  id: number;
  name: string;
  format: "PROPERTIES" | "YAML";
  configFile: string;
  timeCreate: bigint;
  timeUpdate: bigint;
};

export type Release = {
  id: number;
  configSnapshot: string;
  timeCreate: bigint;
  timeUpdate: bigint;
};

export type Pagination = {
  pageIndex: number;
  pageSize: number;
};

export type ServersideDataTableProps<TData, TValue> = {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  pageCount: number;
  pagination: PaginationState;
  onPaginationChange: OnChangeFn<PaginationState>;
};