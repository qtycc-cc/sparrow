import type { Route } from "./+types/app.$id";
import type { PageResponse, ProblemDetail } from "~/types";
import type { ColumnDef } from "@tanstack/react-table";
import { createReactTable, timeStampToDateString } from "~/lib/utils";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "~/components/ui/dropdown-menu";
import { Cog, MoreHorizontal, RefreshCcwIcon } from "lucide-react";
import { DataTable } from "~/components/data-table";
import { DataTablePagination } from "~/components/data-table-pagination";
import { Button } from "~/components/ui/button";
import { Separator } from "~/components/ui/separator";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "~/components/ui/alert-dialog";
import { Form, useActionData } from "react-router";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "~/components/ui/dialog";
import { Label } from "~/components/ui/label";
import { Input } from "~/components/ui/input";
import { useEffect, useState } from "react";
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "~/components/ui/empty";
import { Spinner } from "~/components/ui/spinner";
import { toast } from "sonner";

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
    cell: ({ row }) => {
      const config = row.original;
      const [showPatchDialog, setShowPatchDialog] = useState(false);
      const [showDeleteDialog, setShowDeleteDialog] = useState(false);
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
              <DropdownMenuItem onClick={() => setShowPatchDialog(true)}>编辑</DropdownMenuItem>
              <DropdownMenuItem variant="destructive" onClick={() => setShowDeleteDialog(true)}>删除</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <Dialog open={showPatchDialog} onOpenChange={setShowPatchDialog}>
            <DialogContent className="sm:max-w-[425px]">
              <Form method="patch" onSubmit={() => setShowPatchDialog(false)}>
                <DialogHeader>
                  <DialogTitle>编辑配置</DialogTitle>
                </DialogHeader>
                <DialogDescription className="mb-4 mt-4 text-1xl">
                  修改配置后需要发布才能生效。如果不想改key保持默认即可。
                </DialogDescription>
                <div className="grid gap-4">
                  <div className="grid gap-3">
                    <Label htmlFor="itemKey">key</Label>
                    <Input id="itemKey" name="itemKey" required defaultValue={config.itemKey} />
                  </div>
                  <div className="grid gap-3">
                    <Label htmlFor="itemValue">value</Label>
                    <Input id="itemValue" name="itemValue" required defaultValue={config.itemValue} />
                  </div>
                </div>
                <DialogFooter className="mt-4">
                  <DialogClose asChild>
                    <Button variant="outline">取消</Button>
                  </DialogClose>
                  <input type="hidden" name="id" value={config.id}></input>
                  <Button type="submit" name="action" value="patch">保存</Button>
                </DialogFooter>
              </Form>
            </DialogContent>
          </Dialog>
          <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>确认删除？</AlertDialogTitle>
                <AlertDialogDescription>
                  这将删除该配置项。
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>取消</AlertDialogCancel>
                <Form method="delete">
                  <input type="hidden" name="id" value={config.id} />
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

export async function loader({ params }: Route.LoaderArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/appId/${params.id}`, {
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
    return [] as Config[];
  }
  const data = await response.json() as PageResponse<Config>;
  return data.content;
}

export async function clientAction({
  request,
  params
}: Route.ActionArgs) {
  const formData = await request.formData();
  const { action, ...value } = Object.fromEntries(formData);
  if (action === "delete") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/${value.id}`, {
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
  if (action === "patch") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/${value.id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        itemKey: value.itemKey,
        itemValue: value.itemValue
      })
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("修改失败", {
        description: problemDetail?.detail
      });
      return "Patch failed";
    }
    return "Patch success";
  }
  if (action === "post") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/appId/${params.id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        itemKey: value.itemKey,
        itemValue: value.itemValue
      })
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
  if (action === "release") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/release/appId/${params.id}`, {
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
      return "Release failed";
    }
    return "Release success";
  }
}

export function HydrateFallback() {
  return (
    <div className="container">
      <Spinner className="size-8" />
    </div>
  );
}

export default function AppDetail({
  loaderData,
}: Route.ComponentProps) {
  const actionData = useActionData();
  const configs = loaderData satisfies Config[];
  const table = createReactTable({ columns, data: configs });
  const [showCreateDialog, setShowCreateDialog] = useState(false);

  useEffect(() => {
    if (actionData === "Release success") {
      toast.success("发布成功");
    }
  }, [actionData]);

  return (
    <div className="container mx-auto py-5">
      <div className="container mx-auto flex flex-row justify-between items-center mb-4">
        <h1 className="mb-4 text-2xl font-bold">配置列表</h1>
        <div className="flex justify-around gap-2 self-end">
          <Form method="post">
            <Button className="bg-[#84cc16] hover:bg-[#65a30d]" type="submit" name="action" value="release">发布</Button>
          </Form>
          <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
            <DialogTrigger asChild>
              <Button
                onSelect={(e) => e.preventDefault() /**prevent close the dialog */}
              >
                新增
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <Form method="patch" onSubmit={() => setShowCreateDialog(false)}>
                <DialogHeader>
                  <DialogTitle>新增应用</DialogTitle>
                </DialogHeader>
                <DialogDescription className="mb-4 mt-4 text-1xl">
                  新增应用
                </DialogDescription>
                <div className="grid gap-4">
                  <div className="grid gap-3">
                    <Label htmlFor="itemKey">key</Label>
                    <Input id="itemKey" name="itemKey" required />
                  </div>
                  <div className="grid gap-3">
                    <Label htmlFor="itemValue">value</Label>
                    <Input id="itemValue" name="itemValue" required />
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
      </div>
      {/**Keep the same table */}
      {configs.length ? (
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