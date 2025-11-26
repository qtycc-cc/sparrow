import type { Route } from "./+types/app_.$appId.config";
import type { PageResponse, ProblemDetail } from "~/types";
import type { ColumnDef, Updater } from "@tanstack/react-table";
import { createReactTable, emptyPageResponse, timeStampToDateString } from "~/lib/utils";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "~/components/ui/dropdown-menu";
import { ArrowLeft, Cog, MoreHorizontal, RefreshCcwIcon } from "lucide-react";
import { DataTable } from "~/components/data-table";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Button } from "~/components/ui/button";
import { Separator } from "~/components/ui/separator";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { toast } from "sonner";
import { Loader } from "~/components/loader";
import { Form, Link, Outlet, useNavigate, useSearchParams } from "react-router";

type Config = {
  id: number;
  appId: number;
  itemKey: string;
  itemValue: string;
  timeCreate: bigint;
  timeUpdate: bigint;
};

const columns: ColumnDef<Config>[] = [
  {
    "accessorKey": "id",
    "header": "ID",
  },
  {
    "accessorKey": "itemKey",
    "header": "配置项",
  },
  {
    "accessorKey": "itemValue",
    "header": "配置值",
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
      const config = row.original;
      const navigate = useNavigate();
      return (
        <>
          <DropdownMenu modal={false} >
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <span className="sr-only">Open menu</span>
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => navigate(`action/edit-config/${config.id}${location.search}`)}>编辑</DropdownMenuItem>
              <DropdownMenuItem variant="destructive" onClick={() => navigate(`action/delete-config/${config.id}${location.search}`)}>删除</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </>
      );
    },
  },
];

export async function clientLoader({
  request,
  params,
}: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const pageIndex = Number(url.searchParams.get("pageIndex") || 0);
  const pageSize = Number(url.searchParams.get("pageSize") || 10);
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/appId/${params.appId}?page=${pageIndex}&size=${pageSize}`, {
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
    return emptyPageResponse<Config>();
  }
  const data = await response.json() as PageResponse<Config>;
  return data;
}

export async function clientAction({
  params
}: Route.ClientActionArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/release/appId/${params.appId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    }
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("发布失败", {
      description: problemDetail?.detail
    });
  }
  toast.success("发布成功");
}

export function HydrateFallback() {
  return (
    <Loader></Loader>
  );
}

export default function AppConfigs({
  loaderData,
}: Route.ComponentProps) {
  const navigate = useNavigate();
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
  const configPage = loaderData satisfies PageResponse<Config>;

  const table = createReactTable({
    columns,
    data: configPage.content,
    pageCount: loaderData.page.totalPages,
    pagination: pagination,
    onPaginationChange: handlePaginationChange
  });

  return (
    <div className="container mx-auto py-5">
      <Outlet />
      <div className="container mx-auto flex flex-row justify-between items-center mb-4">
        <div className="flex flex-row items-center gap-2">
          <Link to="/app">
            <ArrowLeft />
          </Link>
          <h1 className="text-2xl font-bold">配置列表</h1>
        </div>
        <div className="flex justify-around gap-2 self-end">
          <Form method="post">
            <Button className="bg-[#84cc16] hover:bg-[#65a30d]" type="submit">发布</Button>
          </Form>
          <Button onClick={() => navigate(`action/create-config${location.search}`)}>新增</Button>
        </div>
      </div>
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
              <EmptyTitle>暂无配置</EmptyTitle>
              <EmptyDescription>
                暂无配置，您可以刷新或新建。
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