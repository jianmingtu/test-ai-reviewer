import api from "AxiosConfig";

/** Returns a Promise of a list of document configurations from Diligen. */
export const getDocumentTypeConfigurations = async (documentType) => {
  const params = { documentType };
  const response = await api.get("/documentTypeConfigurations", { params });
  return response.data;
};

export const submitDocumentTypeConfigurations = async (configJson) => {
  const response = await api.post("/documentTypeConfigurations", JSON.parse(configJson));
  return response.data;
}