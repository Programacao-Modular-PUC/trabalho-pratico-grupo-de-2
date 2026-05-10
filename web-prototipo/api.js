/**
 * URL base da API.
 * - Se você abrir o protótipo pelo próprio backend (http://localhost:8080/index.html),
 *   usa URLs relativas e não há CORS.
 * - Live Server / outra porta: antes do script defina window.API_BASE = 'http://localhost:8080';
 */
function resolveApiBase() {
  if ("API_BASE" in window && window.API_BASE !== undefined && window.API_BASE !== null) {
    return window.API_BASE;
  }
  const host = location.hostname;
  const port = location.port;
  const local =
    host === "localhost" || host === "127.0.0.1" || host === "[::1]" || host === "::1";
  const sameOriginAsApi = local && port === "8080";
  return sameOriginAsApi ? "" : "http://localhost:8080";
}

const API_BASE = resolveApiBase();

async function apiFetch(path, { method = "GET", body } = {}) {
  const opts = {
    method,
    mode: "cors",
    credentials: "omit",
  };
  if (body !== undefined) {
    opts.headers = { "Content-Type": "application/json" };
    opts.body = JSON.stringify(body);
  }
  const url = `${API_BASE}${path}`;
  let res;
  try {
    res = await fetch(url, opts);
  } catch (e) {
    const tentativa =
      API_BASE === "" ? `${location.origin}${path}` : url;
    throw new Error(
      `Sem conexão com a API (${tentativa}). Suba o backend na porta 8080: na pasta backend execute "mvn spring-boot:run" e mantenha o terminal aberto. Erro: ${e.message}`
    );
  }
  const text = await res.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }
  if (!res.ok) {
    const msg =
      data && typeof data === "object" && data.erro != null
        ? data.erro
        : `Erro HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

function fmtMoney(value) {
  const n = Number(value);
  if (Number.isNaN(n)) return String(value);
  return n.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function fmtDate(isoDate) {
  if (!isoDate) return "";
  const [y, m, d] = isoDate.split("-");
  if (!y || !m || !d) return isoDate;
  return `${d}/${m}/${y}`;
}
