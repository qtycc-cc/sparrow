import type { Route } from "./+types/namespace_.$namespaceId.config";
import type {Namespace, ProblemDetail} from "~/types";
import {toast} from "sonner";
import CodeMirror from "@uiw/react-codemirror";
import { langs } from "@uiw/codemirror-extensions-langs";
import {Form, redirect, useNavigate} from "react-router";
import { monokai } from "@uiw/codemirror-theme-monokai";
import {Button} from "~/components/ui/button";
import {Loader} from "~/components/loader";
import {useCallback, useState} from "react";
import {ArrowLeft} from "lucide-react";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/namespace/${params.namespaceId}`, {
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
    return redirect("/namespace");
  }
  return await response.json() as Namespace;
}

export async function clientAction({ request, params }: Route.ClientActionArgs) {
  const formData = await request.formData();
  const { action, ...value } = Object.fromEntries(formData);
  if (action === "update-namespace") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/namespace/${params.namespaceId}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        configFile: value.configFile,
      }),
    });
    if (!response.ok) {
      const problemDetail = await response.json() as ProblemDetail;
      toast.error("修改失败", {
        description: problemDetail?.detail
      });
    } else {
      toast.success("修改成功");
    }
  } else if (action === "release-namespace") {
    const response = await fetch(`${import.meta.env.VITE_SPARROW_HOST}/admin/release/namespaceId/${params.namespaceId}`, {
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
    } else {
      toast.success("发布成功");
    }
  }
}

export function HydrateFallback() {
  return (
    <Loader></Loader>
  );
}

export default function NamespaceConfig({
  loaderData,
}: Route.ComponentProps) {
  const namespace = loaderData satisfies Namespace;
  const navigate = useNavigate();
  const [value, setValue] = useState(namespace.configFile);
  const onChange = useCallback((value: string) => {
    setValue(value);
  }, []);
  return (
    <>
      <div className="container mx-auto py-5">
        <div className="container mx-auto flex flex-row justify-between items-center mb-4">
          <div className="flex flex-row items-center gap-2">
            <ArrowLeft onClick={() => navigate(-1)} />
            <h1 className="text-2xl font-bold">配置详情 {namespace.format}</h1>
          </div>
          <div className="w-2/4 flex flex-row justify-end gap-2">
            <Button onClick={() => navigate(`/namespace/${namespace.id}/release`)}>发布详情</Button>
            <Form method="post">
              <Button type="submit" name="action" value="release-namespace">发布</Button>
            </Form>
            <Form method="post">
              {/* use textarea to avoid missing line break */}
              <textarea hidden readOnly name="configFile" value={value}></textarea>
              <Button type="submit" name="action" value="update-namespace">保存</Button>
            </Form>
          </div>
        </div>
        <CodeMirror
          value={value}
          height="500px"
          theme={monokai}

          extensions={[namespace.format === "YAML" ? langs.yaml() : langs.properties()]}
          onChange={onChange}
        />
      </div>
    </>
  );
}