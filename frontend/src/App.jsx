import React, { useState, useEffect } from 'react';
import { pautaApi, sessaoApi, votoApi } from './services/api';

// Funções utilitárias para geração de CPF para testes
function gerarCpfValido() {
  const rand = () => Math.floor(Math.random() * 9);
  const n = Array.from({ length: 9 }, rand);

  let d1 = n.reduce((acc, curr, idx) => acc + curr * (10 - idx), 0);
  d1 = 11 - (d1 % 11);
  if (d1 >= 10) d1 = 0;
  n.push(d1);

  let d2 = n.reduce((acc, curr, idx) => acc + curr * (11 - idx), 0);
  d2 = 11 - (d2 % 11);
  if (d2 >= 10) d2 = 0;
  n.push(d2);

  return n.join('');
}

function gerarCpfInvalido() {
  const invalidos = [
    '11111111111',
    '22222222222',
    '00000000000',
    '12345678901',
    '98765432100'
  ];
  return invalidos[Math.floor(Math.random() * invalidos.length)];
}

export default function App() {
  const [activeTab, setActiveTab] = useState('pautas');
  const [pautas, setPautas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  // Modais
  const [showNovaPautaModal, setShowNovaPautaModal] = useState(false);
  const [showAbrirSessaoModal, setShowAbrirSessaoModal] = useState(null);
  const [showResultadoModal, setShowResultadoModal] = useState(null);

  // Form Inputs
  const [novoTitulo, setNovoTitulo] = useState('');
  const [novaDescricao, setNovaDescricao] = useState('');
  const [duracaoSessao, setDuracaoSessao] = useState(1);

  // Voto
  const [selectedPautaId, setSelectedPautaId] = useState('');
  const [votoCpf, setVotoCpf] = useState(gerarCpfValido());
  const [votoOpcao, setVotoOpcao] = useState('SIM');
  const [votoLoading, setVotoLoading] = useState(false);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  };

  const carregarPautas = async () => {
    try {
      setLoading(true);
      const data = await pautaApi.listarTodas();
      if (Array.isArray(data)) {
        setPautas(data);
        if (data.length > 0 && !selectedPautaId) {
          setSelectedPautaId(data[0].id.toString());
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarPautas();
    const interval = setInterval(carregarPautas, 8000);
    return () => clearInterval(interval);
  }, []);

  const handleCriarPauta = async (e) => {
    e.preventDefault();
    try {
      const res = await pautaApi.cadastrar(novoTitulo, novaDescricao);
      if (res.id) {
        showToast('Pauta cadastrada com sucesso!');
        setNovoTitulo('');
        setNovaDescricao('');
        setShowNovaPautaModal(false);
        carregarPautas();
      } else {
        showToast(res.message || 'Erro ao cadastrar pauta', 'error');
      }
    } catch (e) {
      showToast('Falha ao conectar com o servidor', 'error');
    }
  };

  const handleAbrirSessao = async (e) => {
    e.preventDefault();
    if (!showAbrirSessaoModal) return;
    try {
      const res = await sessaoApi.abrir(showAbrirSessaoModal.id, parseInt(duracaoSessao, 10) || 1);
      if (res.id) {
        showToast(`Sessão aberta por ${duracaoSessao} minuto(s)!`);
        setShowAbrirSessaoModal(null);
        setDuracaoSessao(1);
        carregarPautas();
      } else {
        showToast(res.message || 'Erro ao abrir sessão', 'error');
      }
    } catch (e) {
      showToast('Falha ao abrir sessão', 'error');
    }
  };

  const handleConsultarResultado = async (pautaId) => {
    try {
      const data = await pautaApi.obterResultado(pautaId);
      if (data.pautaId) {
        setShowResultadoModal(data);
      } else {
        showToast(data.message || 'Erro ao obter resultado', 'error');
      }
    } catch (e) {
      showToast('Erro ao buscar resultado', 'error');
    }
  };

  const handleEnviarVoto = async (e) => {
    e.preventDefault();
    if (!selectedPautaId || !votoCpf) return;
    try {
      setVotoLoading(true);
      const data = await votoApi.votar(selectedPautaId, votoCpf, votoOpcao);
      if (data.id) {
        showToast(`Voto '${data.opcao}' computado com sucesso! Protocolo #${data.id}`);
        setVotoCpf(gerarCpfValido());
        carregarPautas();
      } else {
        showToast(data.message || 'Erro ao registrar voto', 'error');
      }
    } catch (e) {
      showToast('Erro ao conectar com a API', 'error');
    } finally {
      setVotoLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#0b0f19] text-slate-100">
      {/* Header */}
      <header className="sticky top-0 z-40 glass-panel border-b border-slate-800/80 px-6 py-4">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-400 flex items-center justify-center shadow-lg shadow-emerald-500/20 font-bold text-white text-lg">
              🏛️
            </div>
            <div>
              <h1 className="text-xl font-bold tracking-tight bg-gradient-to-r from-white via-slate-200 to-slate-400 bg-clip-text text-transparent">
                Cooperativa Assembleias
              </h1>
              <p className="text-xs text-emerald-400 font-medium flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                Sistema de Gestão e Votação de Pautas
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <nav className="flex items-center gap-1 bg-slate-900/80 p-1 rounded-xl border border-slate-800">
              <button
                onClick={() => setActiveTab('pautas')}
                className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all ${
                  activeTab === 'pautas' ? 'bg-emerald-500 text-white shadow-lg' : 'text-slate-400 hover:text-white'
                }`}
              >
                📊 Painel de Pautas
              </button>
              <button
                onClick={() => setActiveTab('voto')}
                className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all ${
                  activeTab === 'voto' ? 'bg-emerald-500 text-white shadow-lg' : 'text-slate-400 hover:text-white'
                }`}
              >
                🗳️ Terminal de Votação
              </button>
              <a
                href="/swagger-ui.html"
                target="_blank"
                rel="noreferrer"
                className="px-4 py-2 rounded-lg text-sm font-semibold text-slate-400 hover:text-white"
              >
                📖 Swagger API ↗
              </a>
            </nav>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 md:p-8">
        {/* Tab 1: Pautas */}
        {activeTab === 'pautas' && (
          <div className="space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-800">
              <div>
                <h2 className="text-2xl font-extrabold text-white">Assembleias e Pautas</h2>
                <p className="text-sm text-slate-400">Gerencie pautas deliberativas, abra sessões de votação e acompanhe os resultados em tempo real.</p>
              </div>
              <div className="flex items-center gap-3">
                <button
                  onClick={carregarPautas}
                  className="px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-sm font-medium border border-slate-700 transition"
                >
                  🔄 Atualizar
                </button>
                <button
                  onClick={() => setShowNovaPautaModal(true)}
                  className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-500 hover:from-emerald-500 hover:to-teal-400 text-white text-sm font-bold shadow-lg shadow-emerald-500/20 transition"
                >
                  + Nova Pauta
                </button>
              </div>
            </div>

            {loading && pautas.length === 0 ? (
              <div className="text-center py-20 text-slate-500">Carregando pautas...</div>
            ) : pautas.length === 0 ? (
              <div className="text-center py-20 glass-panel rounded-2xl p-8 border border-dashed border-slate-700">
                <div className="text-4xl mb-3">📋</div>
                <h3 className="text-lg font-bold text-white mb-1">Nenhuma pauta cadastrada</h3>
                <p className="text-sm text-slate-400 mb-6">Cadastre a primeira pauta para iniciar a assembleia da cooperativa.</p>
                <button
                  onClick={() => setShowNovaPautaModal(true)}
                  className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-sm font-bold shadow-lg transition"
                >
                  Cadastrar Primeira Pauta
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {pautas.map((pauta) => {
                  const aberta = pauta.sessaoAberta;
                  const temSessao = !!pauta.statusSessao;

                  return (
                    <div key={pauta.id} className="glass-card rounded-2xl p-6 flex flex-col justify-between">
                      <div>
                        <div className="flex items-start justify-between gap-2 mb-3">
                          <span className="text-xs font-mono px-2.5 py-1 rounded-md bg-slate-800 text-slate-400 border border-slate-700">
                            #{pauta.id}
                          </span>
                          {aberta ? (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                              Votação Aberta
                            </span>
                          ) : temSessao ? (
                            <span className="px-3 py-1 rounded-full text-xs font-bold bg-slate-800 text-slate-400 border border-slate-700">
                              Sessão Encerrada
                            </span>
                          ) : (
                            <span className="px-3 py-1 rounded-full text-xs font-bold bg-amber-500/10 text-amber-400 border border-amber-500/30">
                              Aguardando Sessão
                            </span>
                          )}
                        </div>

                        <h3 className="text-lg font-bold text-white mb-2 line-clamp-2">
                          {pauta.titulo}
                        </h3>
                        <p className="text-sm text-slate-400 mb-4 line-clamp-3">
                          {pauta.descricao || "Sem descrição detalhada."}
                        </p>
                      </div>

                      <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between gap-2">
                        {!temSessao ? (
                          <button
                            onClick={() => setShowAbrirSessaoModal(pauta)}
                            className="w-full py-2.5 px-4 rounded-xl bg-emerald-600/20 hover:bg-emerald-600 text-emerald-300 hover:text-white border border-emerald-500/40 text-xs font-bold transition"
                          >
                            ⚡ Abrir Sessão de Votação
                          </button>
                        ) : (
                          <div className="w-full flex items-center gap-2">
                            {aberta && (
                              <button
                                onClick={() => {
                                  setSelectedPautaId(pauta.id.toString());
                                  setActiveTab('voto');
                                }}
                                className="flex-1 py-2 px-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold transition text-center"
                              >
                                🗳️ Votar
                              </button>
                            )}
                            <button
                              onClick={() => handleConsultarResultado(pauta.id)}
                              className="flex-1 py-2 px-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 text-xs font-bold transition text-center"
                            >
                              📊 Ver Resultado
                            </button>
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {/* Tab 2: Terminal de Voto */}
        {activeTab === 'voto' && (
          <div className="max-w-xl mx-auto space-y-6">
            <div className="text-center">
              <h2 className="text-2xl font-extrabold text-white">Terminal do Associado</h2>
              <p className="text-sm text-slate-400">Emita seu voto em uma assembleia ativa com verificação de CPF integrado.</p>
            </div>

            <div className="glass-panel rounded-2xl p-6 md:p-8 space-y-6">
              <form onSubmit={handleEnviarVoto} className="space-y-5">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-300 mb-2">
                    Selecione a Pauta
                  </label>
                  <select
                    value={selectedPautaId}
                    onChange={(e) => setSelectedPautaId(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Selecione uma pauta --</option>
                    {pautas.map(p => (
                      <option key={p.id} value={p.id}>
                        #{p.id} - {p.titulo} {p.sessaoAberta ? "🟢 (Aberta)" : "🔴 (Fechada)"}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <div className="flex items-center justify-between mb-2">
                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-300">
                      CPF do Associado
                    </label>
                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={() => {
                          const novo = gerarCpfValido();
                          setVotoCpf(novo);
                          showToast(`CPF Válido gerado: ${novo}`, 'info');
                        }}
                        className="px-2.5 py-1 rounded-lg bg-emerald-600/20 hover:bg-emerald-600 text-emerald-400 hover:text-white border border-emerald-500/30 text-[11px] font-bold transition"
                      >
                        🎲 Gerar Válido
                      </button>
                      <button
                        type="button"
                        onClick={() => {
                          const invalido = gerarCpfInvalido();
                          setVotoCpf(invalido);
                          showToast(`CPF Inválido gerado: ${invalido}`, 'error');
                        }}
                        className="px-2.5 py-1 rounded-lg bg-rose-600/20 hover:bg-rose-600 text-rose-400 hover:text-white border border-rose-500/30 text-[11px] font-bold transition"
                      >
                        ⚠️ Gerar Inválido
                      </button>
                    </div>
                  </div>
                  <input
                    type="text"
                    value={votoCpf}
                    onChange={(e) => setVotoCpf(e.target.value)}
                    placeholder="Ex: 19839091069"
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-4 py-3 text-sm font-mono text-white focus:outline-none focus:border-emerald-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-300 mb-2">
                    Sua Opção de Voto
                  </label>
                  <div className="grid grid-cols-2 gap-4">
                    <button
                      type="button"
                      onClick={() => setVotoOpcao('SIM')}
                      className={`py-4 rounded-xl font-bold text-sm flex items-center justify-center gap-2 border transition ${
                        votoOpcao === 'SIM'
                          ? 'bg-emerald-600 text-white border-emerald-400 shadow-lg shadow-emerald-500/25'
                          : 'bg-slate-900 text-slate-400 border-slate-700 hover:border-slate-600'
                      }`}
                    >
                      <span className="text-lg">👍</span> Sim
                    </button>
                    <button
                      type="button"
                      onClick={() => setVotoOpcao('NAO')}
                      className={`py-4 rounded-xl font-bold text-sm flex items-center justify-center gap-2 border transition ${
                        votoOpcao === 'NAO'
                          ? 'bg-rose-600 text-white border-rose-400 shadow-lg shadow-rose-500/25'
                          : 'bg-slate-900 text-slate-400 border-slate-700 hover:border-slate-600'
                      }`}
                    >
                      <span className="text-lg">👎</span> Não
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={votoLoading || !selectedPautaId}
                  className="w-full py-3.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-500 hover:from-emerald-500 hover:to-teal-400 text-white font-bold text-sm shadow-xl disabled:opacity-50 transition"
                >
                  {votoLoading ? "Processando..." : "Confirmar e Registrar Voto"}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* Modais */}
        {showNovaPautaModal && (
          <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="glass-panel w-full max-w-md rounded-2xl p-6 border border-slate-700 space-y-5">
              <h3 className="text-lg font-bold text-white">Cadastrar Nova Pauta</h3>
              <form onSubmit={handleCriarPauta} className="space-y-4">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-300 mb-1">Título da Pauta</label>
                  <input
                    type="text"
                    value={novoTitulo}
                    onChange={(e) => setNovoTitulo(e.target.value)}
                    placeholder="Ex: Reforma da Sede e Instalação Solar"
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3.5 py-2.5 text-sm text-white focus:outline-none focus:border-emerald-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-300 mb-1">Descrição</label>
                  <textarea
                    value={novaDescricao}
                    onChange={(e) => setNovaDescricao(e.target.value)}
                    placeholder="Detalhes e justificativa da proposta..."
                    rows="3"
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3.5 py-2.5 text-sm text-white focus:outline-none focus:border-emerald-500"
                  ></textarea>
                </div>
                <div className="flex justify-end gap-3 pt-2">
                  <button type="button" onClick={() => setShowNovaPautaModal(false)} className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-sm transition">Cancelar</button>
                  <button type="submit" className="px-5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-bold transition">Salvar Pauta</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {showAbrirSessaoModal && (
          <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="glass-panel w-full max-w-md rounded-2xl p-6 border border-slate-700 space-y-5">
              <h3 className="text-lg font-bold text-white">Abrir Sessão de Votação</h3>
              <form onSubmit={handleAbrirSessao} className="space-y-4">
                <p className="text-xs text-emerald-400 font-medium">Pauta: #{showAbrirSessaoModal.id} - {showAbrirSessaoModal.titulo}</p>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-slate-300 mb-1">Duração da Sessão (Minutos)</label>
                  <input
                    type="number"
                    min="1"
                    value={duracaoSessao}
                    onChange={(e) => setDuracaoSessao(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3.5 py-2.5 text-sm text-white focus:outline-none focus:border-emerald-500"
                    required
                  />
                  <p className="text-[11px] text-slate-400 mt-1">Tempo padrão regulamentar: 1 minuto.</p>
                </div>
                <div className="flex justify-end gap-3 pt-2">
                  <button type="button" onClick={() => setShowAbrirSessaoModal(null)} className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-sm transition">Cancelar</button>
                  <button type="submit" className="px-5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-bold transition">Iniciar Sessão</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {showResultadoModal && (
          <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="glass-panel w-full max-w-lg rounded-2xl p-6 border border-slate-700 space-y-6">
              <div className="border-b border-slate-800 pb-3">
                <span className="text-xs font-mono text-slate-400">Pauta #{showResultadoModal.pautaId}</span>
                <h3 className="text-xl font-bold text-white">{showResultadoModal.pautaTitulo}</h3>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="bg-slate-900 p-4 rounded-xl text-center border border-slate-800">
                  <span className="text-xs text-slate-400 uppercase tracking-wider block mb-1">Status da Sessão</span>
                  <span className="text-sm font-bold text-white">{showResultadoModal.statusSessao}</span>
                </div>
                <div className="bg-slate-900 p-4 rounded-xl text-center border border-slate-800">
                  <span className="text-xs text-slate-400 uppercase tracking-wider block mb-1">Deliberação Final</span>
                  <span className={`text-sm font-extrabold ${showResultadoModal.resultado === 'APROVADA' ? 'text-emerald-400' : showResultadoModal.resultado === 'REPROVADA' ? 'text-rose-400' : 'text-amber-400'}`}>
                    {showResultadoModal.resultado}
                  </span>
                </div>
              </div>

              <div className="p-4 bg-slate-900/80 rounded-xl space-y-3 border border-slate-800">
                <div className="flex justify-between text-sm font-semibold">
                  <span className="text-slate-300">Total de Votos:</span>
                  <span className="text-white font-mono">{showResultadoModal.totalVotos}</span>
                </div>
                <div className="flex justify-between text-sm font-semibold text-emerald-400">
                  <span>Votos "SIM":</span>
                  <span className="font-mono">{showResultadoModal.votosSim}</span>
                </div>
                <div className="flex justify-between text-sm font-semibold text-rose-400">
                  <span>Votos "NÃO":</span>
                  <span className="font-mono">{showResultadoModal.votosNao}</span>
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <button onClick={() => setShowResultadoModal(null)} className="px-5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-sm font-semibold transition">Fechar</button>
              </div>
            </div>
          </div>
        )}
      </main>

      {toast && (
        <div className={`fixed bottom-6 right-6 z-50 px-5 py-3 rounded-xl border shadow-2xl transition-all ${
          toast.type === 'error' ? 'bg-rose-600 border-rose-400 text-white' : toast.type === 'info' ? 'bg-blue-600 border-blue-400 text-white' : 'bg-emerald-600 border-emerald-400 text-white'
        }`}>
          {toast.message}
        </div>
      )}
    </div>
  );
}
