import { useReactTable, getCoreRowModel } from "@tanstack/react-table";
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import type { PageResponse, ServersideDataTableProps } from "~/types";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function timeStampToDateString(timestamp: bigint): string {
  const date = new Date(Number(timestamp));
  const formatter = new Intl.DateTimeFormat("zh-CN", {
    timeZone: "Asia/Shanghai",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
  return formatter.format(date);
}

export function emptyPageResponse<T>(): PageResponse<T> {
  return {
    content: [],
    page: {
      size: 0,
      number: 0,
      totalElements: 0,
      totalPages: 0,
    },
  };
}

export function createReactTable<TData, TValue>(props: ServersideDataTableProps<TData, TValue>) {
  return useReactTable({
    columns: props.columns,
    data: props.data,
    getCoreRowModel: getCoreRowModel(),
    manualPagination: true,
    pageCount: props.pageCount,
    state: {
      pagination: props.pagination,
    },
    onPaginationChange: props.onPaginationChange,
  });
}