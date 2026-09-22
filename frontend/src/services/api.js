const API_BASE = "http://localhost:8080";

export const pautaApi = {
  listarTodas: async () => {
    const res = await fetch(`${API_BASE}/api/v1/pautas`);
    return res.json();
  },
  cadastrar: async (titulo, descricao) => {
    const res = await fetch(`${API_BASE}/api/v1/pautas`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ titulo, descricao })
    });
    return res.json();
  },
  obterResultado: async (pautaId) => {
    const res = await fetch(`${API_BASE}/api/v1/pautas/${pautaId}/resultado`);
    return res.json();
  }
};

export const sessaoApi = {
  abrir: async (pautaId, duracaoMinutos) => {
    const res = await fetch(`${API_BASE}/api/v1/sessoes`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ pautaId, duracaoMinutos })
    });
    return res.json();
  }
};

export const votoApi = {
  votar: async (pautaId, cpfAssociado, opcao) => {
    const res = await fetch(`${API_BASE}/api/v1/pautas/${pautaId}/votos`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ cpfAssociado, opcao })
    });
    return res.json();
  }
};
