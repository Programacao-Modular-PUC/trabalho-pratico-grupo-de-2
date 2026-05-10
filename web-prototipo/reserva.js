/* global apiFetch */

function showMsg(el, text, ok) {
  el.textContent = text;
  el.classList.remove("hidden");
  el.classList.toggle("msg-ok", !!ok);
  el.classList.toggle("msg-err", !ok);
}

async function carregarClientes() {
  const sel = document.getElementById("sel-cliente");
  sel.innerHTML = '<option value="">Carregando…</option>';
  const rows = await apiFetch("/clientes");
  sel.innerHTML = '<option value="">Selecione…</option>';
  for (const c of rows) {
    const opt = document.createElement("option");
    opt.value = String(c.id);
    opt.textContent = `${c.nome} (#${c.id})`;
    sel.appendChild(opt);
  }
}

async function carregarResidencias() {
  const sel = document.getElementById("sel-residencia");
  sel.innerHTML = '<option value="">Carregando…</option>';
  const rows = await apiFetch("/residencias");
  sel.innerHTML = '<option value="">Selecione…</option>';
  for (const r of rows) {
    const opt = document.createElement("option");
    opt.value = String(r.id);
    opt.textContent = `${r.nome} (#${r.id})`;
    sel.appendChild(opt);
  }
}

async function carregarQuartos(residenciaId) {
  const sel = document.getElementById("sel-quarto");
  if (!residenciaId) {
    sel.innerHTML = '<option value="">Primeiro escolha a residência</option>';
    return;
  }
  sel.innerHTML = '<option value="">Carregando…</option>';
  const rows = await apiFetch(`/quartos?residenciaId=${encodeURIComponent(residenciaId)}`);
  sel.innerHTML = '<option value="">Selecione…</option>';
  for (const q of rows) {
    const opt = document.createElement("option");
    opt.value = String(q.id);
    const extra = q.possuiHidro ? " — hidro" : "";
    const ar = q.possuiAR ? " — AR" : "";
    opt.textContent = `${q.tipo} #${q.id}${ar}${extra}`;
    sel.appendChild(opt);
  }
}

document.getElementById("btn-novo-cliente").addEventListener("click", async () => {
  const msg = document.getElementById("msg-reserva");
  msg.classList.add("hidden");
  const nome = document.getElementById("cli-nome").value.trim();
  const cpf = document.getElementById("cli-cpf").value.trim();
  const email = document.getElementById("cli-email").value.trim();
  if (!nome) {
    showMsg(msg, "Nome do cliente é obrigatório.", false);
    return;
  }
  try {
    await apiFetch("/clientes", {
      method: "POST",
      body: { nome, cpf: cpf || null, email: email || null },
    });
    showMsg(msg, "Cliente cadastrado.", true);
    await carregarClientes();
  } catch (e) {
    showMsg(msg, e.message, false);
  }
});

document.getElementById("sel-residencia").addEventListener("change", (ev) => {
  carregarQuartos(ev.target.value).catch((e) => {
    showMsg(document.getElementById("msg-reserva"), e.message, false);
  });
});

document.getElementById("btn-confirmar-aluguel").addEventListener("click", async () => {
  const msg = document.getElementById("msg-reserva");
  msg.classList.add("hidden");
  const clienteId = document.getElementById("sel-cliente").value;
  const quartoId = document.getElementById("sel-quarto").value;
  const dataInicio = document.getElementById("data-inicio").value;
  const dataFim = document.getElementById("data-fim").value;
  const numeroHospedes = Number(document.getElementById("num-hospedes").value);
  const solicitaBerço = document.getElementById("chk-berco").checked;

  if (!clienteId || !quartoId || !dataInicio || !dataFim) {
    showMsg(msg, "Preencha cliente, quarto e datas.", false);
    return;
  }
  if (!Number.isFinite(numeroHospedes) || numeroHospedes < 1) {
    showMsg(msg, "Número de hóspedes inválido.", false);
    return;
  }

  try {
    const criado = await apiFetch("/alugueis", {
      method: "POST",
      body: {
        clienteId: Number(clienteId),
        quartoId: Number(quartoId),
        dataInicio,
        dataFim,
        numeroHospedes,
        solicitaBerço,
      },
    });
    sessionStorage.setItem("ultimoAluguelId", String(criado.id));
    window.location.href = `recibo.html?id=${encodeURIComponent(criado.id)}`;
  } catch (e) {
    showMsg(msg, e.message, false);
  }
});

(async () => {
  try {
    await Promise.all([carregarClientes(), carregarResidencias()]);
    await carregarQuartos("");
  } catch (e) {
    showMsg(document.getElementById("msg-reserva"), e.message, false);
  }
})();
