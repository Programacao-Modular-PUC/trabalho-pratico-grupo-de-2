/* global apiFetch */

function showMsg(el, text, ok) {
  el.textContent = text;
  el.classList.remove("hidden");
  el.classList.toggle("msg-ok", !!ok);
  el.classList.toggle("msg-err", !ok);
}

function montarEndereco() {
  const rua = document.getElementById("res-endereco").value.trim();
  const num = document.getElementById("res-numero").value.trim();
  const bairro = document.getElementById("res-bairro").value.trim();
  const cep = document.getElementById("res-cep").value.trim();
  const tel = document.getElementById("res-tel").value.trim();
  const email = document.getElementById("res-email").value.trim();
  const parts = [];
  if (rua || num) parts.push([rua, num].filter(Boolean).join(", "));
  if (bairro) parts.push(bairro);
  if (cep) parts.push(`CEP ${cep}`);
  if (tel) parts.push(`Tel. ${tel}`);
  if (email) parts.push(email);
  return parts.join(" · ") || null;
}

async function carregarResidencias() {
  const lista = document.getElementById("lista-residencias");
  const sel = document.getElementById("quarto-residencia-id");
  lista.innerHTML = "<li>Carregando…</li>";
  try {
    const rows = await apiFetch("/residencias");
    lista.innerHTML = "";
    sel.innerHTML = '<option value="">Selecione…</option>';
    for (const r of rows) {
      const li = document.createElement("li");
      li.textContent = `#${r.id} — ${r.nome}${r.endereco ? ` (${r.endereco})` : ""}`;
      lista.appendChild(li);
      const opt = document.createElement("option");
      opt.value = String(r.id);
      opt.textContent = `#${r.id} ${r.nome}`;
      sel.appendChild(opt);
    }
    if (rows.length === 0) lista.innerHTML = "<li>Nenhuma residência cadastrada.</li>";
  } catch (e) {
    lista.innerHTML = "";
    showMsg(document.getElementById("msg-global"), e.message, false);
  }
}

function payloadQuarto() {
  const tipo = document.getElementById("quarto-tipo").value;
  const residenciaId = Number(document.getElementById("quarto-residencia-id").value);
  const valorBase = document.getElementById("quarto-valor-base").value;
  const possuiAR = document.getElementById("quarto-ar").value === "true";
  const possuiHidro = document.getElementById("quarto-hidro").value === "true";

  const base = {
    tipo,
    residenciaId,
    valorBase,
    possuiAR,
    possuiHidro,
  };

  if (tipo === "INDIVIDUAL") {
    return {
      ...base,
      quantidadeCamasSolteiro: Number(document.getElementById("qi-camas").value),
      valorAdicionalPorCama: document.getElementById("qi-valor-cama").value,
    };
  }
  if (tipo === "DUPLO_CASAL") {
    return {
      ...base,
      tipoCamaCasal: document.getElementById("qd-tipo-cama").value,
      adicionalConfortoComum: document.getElementById("qd-ad-comum").value,
      adicionalConfortoQueenKing: document.getElementById("qd-ad-qk").value,
      taxaDiariaBerço: document.getElementById("qd-taxa-berco").value,
    };
  }
  return {
    ...base,
    capacidadeMaxima: Number(document.getElementById("qf-cap").value),
    quantidadeAmbientes: Number(document.getElementById("qf-amb").value),
    percentualExtraMaxLotacaoCheia: document.getElementById("qf-pct-extra").value,
    incrementoDescontoPorHospedeExtra: document.getElementById("qf-inc-extra").value,
    descontoMaximoGrupo: document.getElementById("qf-desc-max").value,
  };
}

function atualizarCamposTipoQuarto() {
  const tipo = document.getElementById("quarto-tipo").value;
  document.getElementById("grp-individual").style.display =
    tipo === "INDIVIDUAL" ? "block" : "none";
  document.getElementById("grp-duplo").style.display =
    tipo === "DUPLO_CASAL" ? "block" : "none";
  document.getElementById("grp-familia").style.display =
    tipo === "FAMILIA" ? "block" : "none";
}

document.getElementById("btn-salvar-residencia").addEventListener("click", async () => {
  const msg = document.getElementById("msg-residencia");
  msg.classList.add("hidden");
  const nome = document.getElementById("res-nome").value.trim();
  const endereco = montarEndereco();
  if (!nome) {
    showMsg(msg, "Informe o nome da residência.", false);
    return;
  }
  try {
    await apiFetch("/residencias", {
      method: "POST",
      body: { nome, endereco: endereco || "" },
    });
    showMsg(msg, "Residência salva com sucesso.", true);
    await carregarResidencias();
  } catch (e) {
    showMsg(msg, e.message, false);
  }
});

document.getElementById("btn-add-quarto").addEventListener("click", async () => {
  const msg = document.getElementById("msg-quarto");
  msg.classList.add("hidden");
  const rid = document.getElementById("quarto-residencia-id").value;
  if (!rid) {
    showMsg(msg, "Selecione a residência.", false);
    return;
  }
  try {
    await apiFetch("/quartos", { method: "POST", body: payloadQuarto() });
    showMsg(msg, "Quarto cadastrado.", true);
  } catch (e) {
    showMsg(msg, e.message, false);
  }
});

document.getElementById("quarto-tipo").addEventListener("change", atualizarCamposTipoQuarto);

document.getElementById("btn-atualizar-listas").addEventListener("click", () => {
  document.getElementById("msg-global").classList.add("hidden");
  carregarResidencias();
});

atualizarCamposTipoQuarto();
carregarResidencias();
