import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { loginSchema, type LoginFormData } from "../../schemas/login.schema"
import { useLogin } from "../../hooks/useAuth"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../../components/ui/card"

export function LoginPage() {
  const loginMutation = useLogin()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  function onSubmit(data: LoginFormData) {
    loginMutation.mutate(data as { email: string; senha: string })
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Acabou Mony</CardTitle>
          <CardDescription>Faça login para acessar sua conta</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" {...register("email")} />
              {errors.email && (
                <p className="text-xs text-red-600">{errors.email.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="senha">Senha</Label>
              <Input id="senha" type="password" {...register("senha")} />
              {errors.senha && (
                <p className="text-xs text-red-600">{errors.senha.message}</p>
              )}
            </div>

            {loginMutation.isError && (
              <p className="text-sm text-red-600">
                {loginMutation.error?.message ?? "Erro ao fazer login"}
              </p>
            )}

            <Button type="submit" className="w-full" disabled={loginMutation.isPending}>
              {loginMutation.isPending ? "Entrando..." : "Entrar"}
            </Button>
          </form>

          <p className="mt-4 text-center text-sm text-neutral-500">
            Não tem conta?{" "}
            <a href="/register" className="text-neutral-900 underline underline-offset-4">
              Cadastre-se
            </a>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
