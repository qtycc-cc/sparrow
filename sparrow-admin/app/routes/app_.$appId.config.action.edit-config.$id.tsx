import type { ProblemDetail } from "~/types";
import type { Route } from "./+types/app_.$appId.config.action.edit-config.$id";
import { toast } from "sonner";
import { Form, redirect, useNavigate } from "react-router";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "~/components/ui/dialog";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import { Label } from "~/components/ui/label";
import { useState } from "react";

type Config = {
  id: number;
  appId: number;
  itemKey: string;
  itemValue: string;
  timeCreate: bigint;
  timeUpdate: bigint;
};

// for default value of config
export async function clientLoader({
  params,
}: Route.ClientActionArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/${params.id}`, {
    method: "GET",
    headers: {
      "Accept": "application/json"
    }
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("加载失败", {
      description: problemDetail?.detail
    });
    return {} as Config;
  }
  return await response.json() as Config;
}

export async function clientAction({
  request,
  params,
}: Route.ClientActionArgs) {
  const formData = await request.formData();
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/${params.id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      itemKey: formData.get("itemKey"),
      itemValue: formData.get("itemValue")
    })
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("修改失败", {
      description: problemDetail?.detail
    });
  }
  return redirect(`/app/${params.appId}/config`);
}

export default function EditConfig({
  loaderData,
}: Route.ComponentProps) {
  const navigate = useNavigate();
  const [showPatchDialog, setShowPatchDialog] = useState(true);
  const config = loaderData satisfies Config;
  return (
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
              <Button variant="outline" onClick={() => navigate(-1)}>取消</Button>
            </DialogClose>
            <Button type="submit">保存</Button>
          </DialogFooter>
        </Form>
      </DialogContent>
    </Dialog>
  );
}