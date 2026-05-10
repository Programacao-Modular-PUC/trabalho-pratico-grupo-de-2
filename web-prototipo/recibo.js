/* global apiFetch, fmtMoney, fmtDate */

function textoRecibo(a) {
  const diarias = a.quantidadeDiarias ?? "?";
  return [
    `Cliente: ${a.clienteNome} (#${a.clienteId})`,
    `Quarto: ${a.quarto?.tipo ?? ""} #${a.quartoId}`,
    `Data de entrada: ${fmtDate(a.dataInicio)}`,
    `Data de saída: ${fmtDate(a.dataFim)}`,
    `Número de diárias: ${diarias}`,
    `Valor da diária (calculada): ${fmtMoney(a.valorDiariaCalculada)}`,
    `Total à pagar: ${fmtMoney(a.valorTotal)}`,
  ].join("\n");
}

(async () => {
  const el = document.getElementById("recibo-conteudo");
  const params = new URLSearchParams(window.location.search);
  let id = params.get("id");
  if (!id) id = sessionStorage.getItem("ultimoAluguelId");
  if (!id) {
    el.textContent =
      "Nenhum aluguel selecionado. Abra esta página após confirmar uma reserva ou use ?id= na URL.";
    return;
  }
  try {
    const a = await apiFetch(`/alugueis/${encodeURIComponent(id)}`);
    el.textContent = textoRecibo(a);
  } catch (e) {
    el.textContent = `Não foi possível carregar o recibo: ${e.message}`;
  }
})();
