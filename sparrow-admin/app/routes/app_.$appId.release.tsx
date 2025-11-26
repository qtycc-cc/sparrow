import type { PageResponse, ProblemDetail } from "~/types";
import type { Route } from "./+types/app_.$appId.release";
import { createReactTable, emptyPageResponse, timeStampToDateString } from "~/lib/utils";
import { toast } from "sonner";
import type { ColumnDef, Updater } from "@tanstack/react-table";
import { Loader } from "~/components/loader";
import { useSearchParams } from "react-router";
import { DataTable } from "~/components/data-table";
import { Separator } from "~/components/ui/separator";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { Cog, RefreshCcwIcon } from "lucide-react";
import { Button } from "~/components/ui/button";
import JsonView from "@uiw/react-json-view";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "~/components/ui/dialog";
import { monokaiTheme } from "@uiw/react-json-view/monokai";

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
      return (
        <Dialog>
          <DialogTrigger asChild>
            <Button variant="ghost">展示</Button>
          </DialogTrigger>
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
  const configPage = loaderData satisfies PageResponse<Release>;

  const table = createReactTable({
    columns,
    data: configPage.content,
    pageCount: loaderData.page.totalPages,
    pagination: pagination,
    onPaginationChange: handlePaginationChange
  });
  return (
    <div className="container mx-auto py-5">
      {/**Keep the same table */}
      {configPage.content.length ? (
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