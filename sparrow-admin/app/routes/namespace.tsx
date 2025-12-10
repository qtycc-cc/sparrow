import type { Route } from "./+types/namespace";
import { type ColumnDef, type Updater } from "@tanstack/react-table";
import type { PageResponse, Pagination, ProblemDetail, Namespace } from "~/types";
import { DataTable } from "~/components/data-table";
import { createReactTable, emptyPageResponse, timeStampToDateString } from "~/lib/utils";
import { Button } from "~/components/ui/button";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Separator } from "~/components/ui/separator";
import { Grid2x2Check, MoreHorizontal, RefreshCcwIcon } from "lucide-react";
import {Form, Link, useSearchParams} from "react-router";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "~/components/ui/dropdown-menu";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { toast } from "sonner";
import { Loader } from "~/components/loader";
import {useState} from "react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle
} from "~/components/ui/alert-dialog";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from "~/components/ui/dialog";
import {Label} from "~/components/ui/label";
import {Input} from "~/components/ui/input";
import {RadioGroup, RadioGroupItem} from "~/components/ui/radio-group";

const columns: ColumnDef<Namespace>[] = [
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
      const namespace = row.original;
      const [showDeleteDialog, setShowDeleteDialog] = useState(false);
      return (
        <>
          <DropdownMenu modal={false}>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <span className="sr-only">Open menu</span>
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>
                <Link className="w-full" to={`/namespace/${namespace.id}/config`}>配置详情</Link>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Link className="w-full" to={`/namespace/${namespace.id}/release`}>发布详情</Link>
              </DropdownMenuItem>
              <DropdownMenuItem variant="destructive" onClick={() => setShowDeleteDialog(true)}>
                删除
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>确认删除？</AlertDialogTitle>
                <AlertDialogDescription>
                  此操作无法撤销。这将永久删除该命名空间及其所有配置和发布。
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>取消</AlertDialogCancel>
                <Form method="delete">
                  <input hidden name={"id"} value={namespace.id}></input>
                  <AlertDialogAction type="submit" name="action" value="delete-namespace">
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
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/namespace?page=${pageIndex}&size=${pageSize}`, {
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
    return emptyPageResponse<Namespace>();
  }
  return await response.json() as PageResponse<Namespace>;
}

export async function clientAction({
  request,
}: Route.ClientActionArgs) {
  const formData = await request.formData();
  const { action, ...value } = Object.fromEntries(formData);
  if (action === "create-namespace") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/namespace`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: value.name,
        format: value.format,
      }),
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("创建失败", {
        description: problemDetail?.detail
      });
    } else {
      toast.success("创建成功");
    }
  } else if (action === "delete-namespace") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/namespace/${value.id}`, {
      method: "DELETE"
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("删除失败", {
        description: problemDetail?.detail
      });
    } else {
      toast.success("删除成功");
    }
  }
}

export function HydrateFallback() {
  return (
    <Loader></Loader>
  );
}

export default function Namespace({
  loaderData,
}: Route.ComponentProps) {
  const [showCreateDialog, setShowCreateDialog] = useState(false);
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
  const namespacePage = loaderData satisfies PageResponse<Namespace>;
  const table = createReactTable({
    columns,
    data: namespacePage.content,
    pageCount: loaderData.page.totalPages,
    pagination: pagination,
    onPaginationChange: handlePaginationChange
  });

  return (
    <div className="container mx-auto py-5">
      <div className="container mx-auto flex flex-row justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">命名空间列表</h1>
        <Button onClick={() => setShowCreateDialog(true)}>新增</Button>
        <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
          <DialogContent className="sm:max-w-[425px]">
            <Form method="post" onSubmit={() => setShowCreateDialog(false)}>
              <DialogHeader>
                <DialogTitle>新增命名空间</DialogTitle>
              </DialogHeader>
              <DialogDescription className="mb-4 mt-4 text-1xl">
                新增命名空间
              </DialogDescription>
              <div className="grid gap-4">
                <div className="grid gap-3">
                  <Label htmlFor="namespaceName">name</Label>
                  <Input required id="namespaceName" name="name" />
                  <RadioGroup name="format" defaultValue="PROPERTIES">
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="PROPERTIES" id="properties" />
                      <Label htmlFor="properties">properties</Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="YAML" id="yaml" />
                      <Label htmlFor="yaml">yaml</Label>
                    </div>
                  </RadioGroup>
                </div>
              </div>
              <DialogFooter className="mt-4">
                <DialogClose asChild>
                  <Button variant="outline">取消</Button>
                </DialogClose>
                <Button type="submit" name={"action"} value={"create-namespace"}>保存</Button>
              </DialogFooter>
            </Form>
          </DialogContent>
        </Dialog>
      </div>
      {namespacePage.content.length ? (
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
              <EmptyTitle>暂无命名空间</EmptyTitle>
              <EmptyDescription>
                暂无命名空间，您可以刷新或新建。
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
