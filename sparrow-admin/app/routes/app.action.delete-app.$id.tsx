import { useState } from "react";
import { Form, redirect, useNavigate } from "react-router";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "~/components/ui/alert-dialog";
import type { Route } from "./+types/app.action.delete-app.$id";
import type { ProblemDetail } from "~/types";
import { toast } from "sonner";

export async function clientAction({
  params,
}: Route.ClientActionArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/app/${params.id}`, {
    method: "DELETE"
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("删除失败", {
      description: problemDetail?.detail
    });
  }
  return redirect("/app");
}

export default function DeleteApp() {
  const navigate = useNavigate();
  const [showDeleteDialog, setShowDeleteDialog] = useState(true);
  return (
    <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除？</AlertDialogTitle>
          <AlertDialogDescription>
            此操作无法撤销。这将永久删除该应用及其所有配置和发布。
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={() => navigate(-1)}>取消</AlertDialogCancel>
          <Form method="delete">
            <AlertDialogAction type="submit">
              确认删除
            </AlertDialogAction>
          </Form>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}