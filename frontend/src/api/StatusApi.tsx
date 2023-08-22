import { z } from "zod";

const StatusResponse = z.object({
  email: z.string(),
  fullName: z.string().optional(),
  status: z.string(),
});

type StatusResponse = z.infer<typeof StatusResponse>;

const checklogin = async (): Promise<StatusResponse | false> => {
  const response = await fetch("http://localhost:8080/status", {
    method: "GET",
    credentials: "include",
  });
  console.log("faile tog et status");
  if (response.status != 200) {
    return false;
  }
  const json = await response.json();
  const parsedResponse = StatusResponse.parse(json);
  return parsedResponse;
};

export { checklogin };
export type { StatusResponse };
