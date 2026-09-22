# 📱 Aplicativo Mobile - Cooperativa Votação

Aplicativo para celular (Android / iOS / PWA) com **todas as funcionalidades do sistema de votação** e **configuração dinâmica de IP do servidor com validação de conexão em tempo real**.

---

## 🌟 Recursos do Aplicativo Mobile

1. **🔒 Validação de Conexão com o Servidor (Gatekeeper)**:
   - Permite informar o IP/URL do computador onde o backend está rodando (ex: `http://192.168.1.100:8080` ou `http://10.0.2.2:8080` no emulador).
   - **Valida ativamente se o servidor está online e respondendo antes de liberar o acesso às funções**.
   - Salva a configuração localmente para conexões futuras.
   - Botão no topo (`⚙️ IP: ...`) para reconfigurar a qualquer momento.

2. **📊 Pautas & Sessões de Votação**:
   - Listagem de todas as pautas em cards com status dinâmico.
   - Cadastro de nova pauta.
   - Abertura de sessão de votação com configuração do tempo de duração em minutos.

3. **🗳️ Terminal do Associado & Voto**:
   - Seleção da pauta aberta.
   - **🎲 Botão para Gerar CPF Válido** (para testes ágeis e votos aprovados).
   - **⚠️ Botão para Gerar CPF Inválido** (para testes de rejeição e validação).
   - Voto interativo "Sim" 👍 ou "Não" 👎 com comprovante e protocolo de votação.

4. **📊 Apuração e Consulta de Resultados**:
   - Consulta detalhada: total de votos, votos Sim, votos Não, status e deliberação final.

---

## 🚀 Como Rodar e Instalar no Celular

### Opção 1: Instalação Direta no Celular via PWA (Sem precisar compilar)
1. Certifique-se de que o backend Java está rodando (`mvn spring-boot:run` ou `docker-compose up`).
2. Descubra o IP do seu computador na rede Wi-Fi (`ipconfig` no Windows, ex: `192.168.1.100`).
3. No celular conectado no mesmo Wi-Fi, abra o Chrome e acesse:
   ```
   http://192.168.1.100:8080/
   ```
4. Toque nos 3 pontinhos do Chrome e escolha **"Adicionar à tela de início"** ou **"Instalar Aplicativo"**.
5. O app será instalado no seu celular como um aplicativo independente!

---

### Opção 2: Gerar o APK com Android Studio / Gradle
1. Abra o **Android Studio**.
2. Clique em **Open Project** e selecione a pasta `c:\Projetos\Tarefa\mobile-app\android`.
3. Vá no menu superior: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. O APK gerado estará em:
   ```
   mobile-app/android/app/build/outputs/apk/debug/app-debug.apk
   ```
5. Transfira o arquivo `.apk` para o celular e instale!

---

### Opção 3: Usando o Capacitor CLI
```bash
cd mobile-app
npm install
npx cap sync android
npx cap open android
```
No Android Studio aberto pelo Capacitor, clique em **Run (Play)** com o celular ou emulador conectado.

---

## ⚙️ Dica de Rede e Firewall
Para que o celular consiga acessar o computador na rede local:
- O celular e o computador devem estar conectados na **mesma rede Wi-Fi**.
- Caso o Windows Defender Firewall bloqueie a porta `8080`, permita o tráfego da porta no firewall ou execute em PowerShell como Administrador:
  ```powershell
  New-NetFirewallRule -DisplayName "Spring Boot 8080" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
  ```
