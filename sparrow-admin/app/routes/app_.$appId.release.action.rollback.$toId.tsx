import { Form, redirect, useNavigate } from "react-router";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "~/components/ui/alert-dialog";
import type { Route } from "./+types/app_.$appId.release.action.rollback.$toId";
import { useState } from "react";
import { toast } from "sonner";
import type { ProblemDetail } from "~/types";

export async function clientAction({
  params,
}: Route.ClientActionArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/release/appId/${params.appId}/rollback?toId=${params.toId}`, {
    method: "POST"
  });
  if (!response.ok) {
    const problemDetail = await response.json() as ProblemDetail;
    toast.error("回滚失败", {
      description: problemDetail?.detail
    });
  }
  return redirect(`/app/${params.appId}/release`);
}

export default function RollBackRelease() {
  const navigate = useNavigate();
  const [showRollBackDialog, setShowRollBackeDialog] = useState(true);
  return (
    <AlertDialog open={showRollBackDialog} onOpenChange={setShowRollBackeDialog}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>确认回滚？</AlertDialogTitle>
          <AlertDialogDescription>
            回滚最新配置（如果您选择最新的发布）或者回滚到您所选的发布
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={() => navigate(-1)}>取消</AlertDialogCancel>
          <Form method="post">
            <AlertDialogAction type="submit">
              确认回滚
            </AlertDialogAction>
          </Form>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}