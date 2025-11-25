import type { Route } from "./+types/app";
import { type ColumnDef, type Updater } from "@tanstack/react-table";
import type { PageResponse, Pagination, ProblemDetail } from "~/types";
import { DataTable } from "~/components/data-table";
import { createReactTable, emptyPageResponse, timeStampToDateString } from "~/lib/utils";
import { Button } from "~/components/ui/button";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Separator } from "~/components/ui/separator";
import { Grid2x2Check, MoreHorizontal, RefreshCcwIcon } from "lucide-react";
import { Form, Link, useSearchParams } from "react-router";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "~/components/ui/alert-dialog";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "~/components/ui/dialog";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "~/components/ui/dropdown-menu";
import { Label } from "~/components/ui/label";
import { Input } from "~/components/ui/input";
import { useState } from "react";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { toast } from "sonner";
import { Loader } from "~/components/loader";

type App = {
  id: number;
  name: string;
  timeCreate: bigint;
  timeUpdate: bigint;
};

const columns: ColumnDef<App>[] = [
  {
    "accessorKey": "id",
    "header": "ID",
  },
  {
    "accessorKey": "name",
    "header": "名称",
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
      const app = row.original;
      const [showDeleteDialog, setShowDeleteDialog] = useState(false);
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
              <DropdownMenuItem>
                <Link className="w-full" to={`/app/${app.id}/config`}>详情</Link>
              </DropdownMenuItem>
              <DropdownMenuItem variant="destructive" onSelect={() => setShowDeleteDialog(true)}>
                删除
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>确认删除？</AlertDialogTitle>
                <AlertDialogDescription>
                  此操作无法撤销。这将永久删除该应用及其所有配置和发布。
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>取消</AlertDialogCancel>
                <Form method="delete">
                  <input type="hidden" name="id" value={app.id} />
                  <AlertDialogAction type="submit" name="action" value="delete">
                    确认删除
                  </AlertDialogAction>
                </Form>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </>
      );
    },
  },
];

export async function clientLoader({
  request,
}: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const pageIndex = Number(url.searchParams.get("pageIndex") || 0);
  const pageSize = Number(url.searchParams.get("pageSize") || 10);
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/app?page=${pageIndex}&size=${pageSize}`, {
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
    return emptyPageResponse<App>();
  }
  const data = await response.json() as PageResponse<App>;
  return data;
}

export async function clientAction({
  request,
}: Route.ClientActionArgs) {
  const formData = await request.formData();
  const { action, ...value } = Object.fromEntries(formData);
  if (action === "delete") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/app/${value.id}`, {
      method: "DELETE"
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("删除失败", {
        description: problemDetail?.detail
      });
      return "Delete failed";
    }
    return "Delete success";
  }
  if (action === "post") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/app`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: value.name,
      }),
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("创建失败", {
        description: problemDetail?.detail
      });
      return "Create failed";
    }
    return "Create success";
  }
}

export function HydrateFallback() {
  return (
    <Loader></Loader>
  );
}

export default function App({
  loaderData,
}: Route.ComponentProps) {
  const [, setSearchParams] = useSearchParams();
  const pagination: Pagination = {
    pageIndex: loaderData.page.number,
    pageSize: loaderData.page.size,
  };
  function handlePaginationChange(updater: Updater<Pagination>) {
    const newState =
      typeof updater === "function"
        ? updater(pagination)
        : updater;

    setSearchParams({
      pageIndex: newState.pageIndex.toString(),
      pageSize: newState.pageSize.toString(),
    });
  }
  const appPage = loaderData satisfies PageResponse<App>;
  const table = createReactTable({
    columns,
    data: appPage.content,
    pageCount: loaderData.page.totalPages,
    pagination: pagination,
    onPaginationChange: handlePaginationChange
  });

  const [showCreateDialog, setShowCreateDialog] = useState(false);

  return (
    <div className="container mx-auto py-5">
      <div className="container mx-auto flex flex-row justify-between items-center mb-4">
        <h1 className="mb-4 text-2xl font-bold">应用列表</h1>
        <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
          <DialogTrigger asChild>
            <Button
              onSelect={(e) => e.preventDefault() /**prevent close the dialog */}
            >
              新增
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[425px]">
            <Form method="post" onSubmit={() => setShowCreateDialog(false)}>
              <DialogHeader>
                <DialogTitle>新增应用</DialogTitle>
              </DialogHeader>
              <DialogDescription className="mb-4 mt-4 text-1xl">
                新增应用
              </DialogDescription>
              <div className="grid gap-4">
                <div className="grid gap-3">
                  <Label htmlFor="appName">name</Label>
                  <Input required id="appName" name="name" />
                </div>
              </div>
              <DialogFooter className="mt-4">
                <DialogClose asChild>
                  <Button variant="outline">取消</Button>
                </DialogClose>
                <Button type="submit" name="action" value="post">保存</Button>
              </DialogFooter>
            </Form>
          </DialogContent>
        </Dialog>
      </div>
      {appPage.content.length ? (
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
                <Grid2x2Check />
              </EmptyMedia>
              <EmptyTitle>暂无应用</EmptyTitle>
              <EmptyDescription>
                暂无应用，您可以刷新或新建。
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
