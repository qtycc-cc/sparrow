import type { PageResponse, ProblemDetail } from "~/types";
import type { Route } from "./+types/app_.$appId.release";
import { createReactTable, emptyPageResponse, timeStampToDateString } from "~/lib/utils";
import { toast } from "sonner";
import type { ColumnDef, Updater } from "@tanstack/react-table";
import { Loader } from "~/components/loader";
import { Link, Outlet, useNavigate, useSearchParams } from "react-router";
import { DataTable } from "~/components/data-table";
import { Separator } from "~/components/ui/separator";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { ArrowLeft, Cog, MoreHorizontal, RefreshCcwIcon } from "lucide-react";
import { Button } from "~/components/ui/button";
import JsonView from "@uiw/react-json-view";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "~/components/ui/dialog";
import { monokaiTheme } from "@uiw/react-json-view/monokai";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "~/components/ui/dropdown-menu";
import { useState } from "react";

type Release = {
  id: number;
  appId: number;
  configSnapshot: string
  timeCreate: bigint;
  timeUpdate: bigint;
};

const columns: ColumnDef<Release>[] = [
  {
    "accessorKey": "id",
    "header": "ID",
  },
  {
    "accessorKey": "timeCreate",
    "header": "创建时间",
    cell: ({ row }) => {
      return timeStampToDateString(row.original.timeCreate);
    }
  },
  {
    "accessorKey": "timeUpdate",
    "header": "更新时间",
    cell: ({ row }) => {
      return timeStampToDateString(row.original.timeUpdate);
    }
  },
  {
    id: "actions",
    "header": "操作",
    cell: ({ row }) => {
      const release = row.original;
      const navigate = useNavigate();
      const [showDialog, setShowDialog] = useState(false);
      return (
        <>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <span className="sr-only">Open menu</span>
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setShowDialog(true)}>
                展示
              </DropdownMenuItem>
              <DropdownMenuItem variant="destructive" onClick={() => navigate(`action/rollback/${release.id}${location.search}`)}>
                回滚
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <Dialog open={showDialog} onOpenChange={setShowDialog}>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>JSON View</DialogTitle>
                <DialogDescription>
                  JSON格式化展示
                </DialogDescription>
              </DialogHeader>
              <JsonView value={JSON.parse(release.configSnapshot)} style={monokaiTheme}></JsonView>
            </DialogContent>
          </Dialog>
        </>
      );
    },
  },
];

export async function clientLoader({
  request,
  params
}: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const pageIndex = Number(url.searchParams.get("pageIndex") || 0);
  const pageSize = Number(url.searchParams.get("pageSize") || 10);
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/release/appId/${params.appId}?page=${pageIndex}&size=${pageSize}`, {
    method: "GET",
    headers: {
      "Accept": "application/json",
    }
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("加载失败", {
      description: problemDetail?.detail
    });
    return emptyPageResponse<Release>();
  }
  const data = await response.json() as PageResponse<Release>;
  return data;
}

export function HydrateFallback() {
  return (
    <Loader></Loader>
  );
}

export default function AppReleases({
  loaderData
}: Route.ComponentProps) {
  const [, setSearchParams] = useSearchParams();
  const pagination = {
    pageIndex: loaderData.page.number,
    pageSize: loaderData.page.size,
  };
  function handlePaginationChange(updater: Updater<typeof pagination>) {
    const newState =
      typeof updater === "function"
        ? updater(pagination)
        : updater;

    setSearchParams({
      pageIndex: newState.pageIndex.toString(),
      pageSize: newState.pageSize.toString(),
    });
  }
  const releasePage = loaderData satisfies PageResponse<Release>;

  const table = createReactTable({
    columns,
    data: releasePage.content,
    pageCount: loaderData.page.totalPages,
    pagination: pagination,
    onPaginationChange: handlePaginationChange
  });
  return (
    <div className="container mx-auto py-5">
      <Outlet />
      <div className="flex flex-row items-center gap-2 mb-4">
        <Link to="/app">
          <ArrowLeft />
        </Link>
        <h1 className="text-2xl font-bold">发布列表</h1>
      </div>
      {/**Keep the same table */}
      {releasePage.content.length ? (
        <>
          <DataTable table={table} />
          <Separator className="my-4" />
          <DataTablePagination table={table} />
        </>
      ) : (
        <>
          <Empty className="from-muted/50 to-background h-full bg-linear-to-b from-30%">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <Cog />
              </EmptyMedia>
              <EmptyTitle>暂无发布</EmptyTitle>
              <EmptyDescription>
                暂无发布
              </EmptyDescription>
            </EmptyHeader>
            <EmptyContent className="flex flex-row justify-center items-center">
              <Button variant="outline" size="sm" onClick={() => location.reload()}>
                <RefreshCcwIcon />
                刷新
              </Button>
            </EmptyContent>
          </Empty>
        </>
      )}
    </div>
  );
}