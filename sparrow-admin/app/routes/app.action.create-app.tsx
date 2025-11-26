import { useState } from "react";
import type { Route } from "./+types/app.action.create-app";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "~/components/ui/dialog";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import { Label } from "~/components/ui/label";
import { Form, redirect, useNavigate } from "react-router";
import type { ProblemDetail } from "~/types";
import { toast } from "sonner";

export async function clientAction({
  request,
}: Route.ClientActionArgs) {
  const formData = await request.formData();
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/app`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      name: formData.get("name"),
    }),
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("创建失败", {
      description: problemDetail?.detail
    });
  }
  return redirect("/app");
}

export default function CreateApp() {
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
              <Label htmlFor="appName">name</Label>
              <Input required id="appName" name="name" />
            </div>
          </div>
          <DialogFooter className="mt-4">
            <DialogClose asChild>
              <Button variant="outline" onClick={() => navigate("/app")}>取消</Button>
            </DialogClose>
            <Button type="submit">保存</Button>
          </DialogFooter>
        </Form>
      </DialogContent>
    </Dialog>
  );
}