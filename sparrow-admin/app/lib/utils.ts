import { useReactTable, getCoreRowModel, getPaginationRowModel } from "@tanstack/react-table";
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import type { DataTableProps } from "~/types";

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

export function createReactTable<TData, TValue>({
  columns,
  data,
}: DataTableProps<TData, TValue>) {
  return useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
  });
}
