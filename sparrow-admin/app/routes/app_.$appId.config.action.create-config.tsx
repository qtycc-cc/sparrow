
import { useState } from "react";
import { Form, redirect, useNavigate } from "react-router";
import { Button } from "~/components/ui/button";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "~/components/ui/dialog";
import { Input } from "~/components/ui/input";
import { Label } from "~/components/ui/label";
import type { Route } from "./+types/app_.$appId.config.action.create-config";
import { toast } from "sonner";
import type { ProblemDetail } from "~/types";

export async function clientAction({
  request,
  params,
}: Route.ClientActionArgs) {
  const formData = await request.formData();
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/appId/${params.appId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      itemKey: formData.get("itemKey"),
      itemValue: formData.get("itemValue"),
    })
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("创建失败", {
      description: problemDetail?.detail
    });
  }
  return redirect(`/app/${params.appId}/config`);
}

export default function CreateConfig() {
  const navigate = useNavigate();
  const [showCreateDialog, setShowCreateDialog] = useState(true);
  return (
    <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
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
              <Button variant="outline" onClick={() => navigate(-1)}>取消</Button>
            </DialogClose>
            <Button type="submit" name="action" value="post">保存</Button>
          </DialogFooter>
        </Form>
      </DialogContent>
    </Dialog>
  );
}