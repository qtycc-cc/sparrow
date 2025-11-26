import { useState } from "react";
import { Form, redirect, useNavigate } from "react-router";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "~/components/ui/alert-dialog";
import type { Route } from "./+types/app_.$appId.config.action.delete-config.$id";
import { toast } from "sonner";
import type { ProblemDetail } from "~/types";

export async function clientAction({
  params,
}: Route.ClientActionArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/config/${params.id}`, {
    method: "DELETE"
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("删除失败", {
      description: problemDetail?.detail
    });
  }
  return redirect(`/app/${params.appId}/config`);
}

export default function DeleteConfig() {
  const navigate = useNavigate();
  const [showDeleteDialog, setShowDeleteDialog] = useState(true);
  return (
    <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认删除？</AlertDialogTitle>
          <AlertDialogDescription>
            这将删除该配置项。
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