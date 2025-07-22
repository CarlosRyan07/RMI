# Sistema de Chat RMI

## Descrição
Sistema de chat distribuído implementado com Java RMI que combina arquitetura cliente/servidor e P2P.

## Funcionalidades
✅ **Registro de clientes** com @ único
✅ **Mensagens privadas** entre clientes (P2P)
✅ **Mensagens de sala** (broadcast para todos)
✅ **Envio de arquivos** entre clientes
✅ **Lista de clientes ativos**
✅ **Conexão/desconexão automática**

## Arquitetura
- **Servidor Central**: Gerencia registro de clientes e facilita descoberta (cliente/servidor)
- **Comunicação P2P**: Mensagens e arquivos são trocados diretamente entre clientes
- **RMI Registry**: Cada cliente cria seu próprio registry para receber conexões P2P

## Como Executar

### 1. Compilar o projeto
```bash
javac *.java
```

### 2. Iniciar o Servidor de Chat
```bash
java ServidorChat
```
O servidor será iniciado na porta 1099 e aguardará conexões.

### 3. Iniciar Clientes
```bash
java ClienteChat
```

O cliente solicitará:
- **@ (apelido)**: Identificador único no chat
- **Porta P2P**: Porta para receber conexões (ex: 1100, 1101, 1102...)
- **IP do servidor**: IP onde está o ServidorChat (Enter para localhost)

### 4. Comandos do Chat

#### Mensagem Privada
```
/msg @usuario sua mensagem aqui
```

#### Mensagem para Sala (Broadcast)
```
/sala mensagem para todos
```

#### Enviar Arquivo
```
/arquivo @usuario caminho/do/arquivo.txt
```

#### Listar Clientes Ativos
```
/listar
```

#### Sair do Chat
```
/sair
```

## Exemplo de Uso

### Terminal 1 - Servidor
```bash
C:\RMI> java ServidorChat
RMI registry criado na porta 1099.
Servidor de Chat iniciado!
=== SERVIDOR DE CHAT RMI ===
Servidor registrado como 'ServidorChat'
Aguardando conexões de clientes...
============================
```

### Terminal 2 - Cliente João
```bash
C:\RMI> java ClienteChat
=== CLIENTE DE CHAT RMI ===
Digite seu @ (apelido único): joao
Digite a porta para P2P (ex: 1100): 1100
IP do servidor de chat (Enter para localhost): 
✓ Servidor P2P iniciado em 192.168.1.100:1100
✓ Conectado ao servidor de chat como @joao

=== CHAT RMI - @joao ===
Comandos disponíveis:
  /msg @usuario mensagem     - Enviar mensagem privada
  /sala mensagem             - Enviar mensagem para sala
  /arquivo @usuario caminho  - Enviar arquivo
  /listar                    - Listar clientes ativos
  /sair                      - Sair do chat
================================
>> /sala Olá pessoal!
✓ Mensagem enviada para a sala
>> 
```

### Terminal 3 - Cliente Maria
```bash
C:\RMI> java ClienteChat
=== CLIENTE DE CHAT RMI ===
Digite seu @ (apelido único): maria
Digite a porta para P2P (ex: 1100): 1101
IP do servidor de chat (Enter para localhost): 
✓ Servidor P2P iniciado em 192.168.1.100:1101
✓ Conectado ao servidor de chat como @maria

=== CHAT RMI - @maria ===
[...]
>> 
[SALA] @joao: Olá pessoal!
>> /msg joao Oi João! Tudo bem?
✓ Mensagem enviada para @joao
>> 
```

## Estrutura do Projeto

### Interfaces RMI
- `ServidorChatRemoto.java`: Interface do servidor central
- `ClienteChatRemoto.java`: Interface para comunicação P2P

### Implementações
- `ServidorChat.java`: Servidor central que gerencia clientes
- `ClienteChat.java`: Cliente com interface de usuário e servidor P2P
- `InfoCliente.java`: Classe para armazenar dados dos clientes

### Arquivos Originais (HelloRMI)
- `Servidor.java`, `Cliente.java`, `ServidorRemoto.java`: Exemplo básico RMI
- `rmi.policy`: Arquivo de políticas de segurança

## Características Técnicas

### Comunicação Transiente e Síncrona
- RMI garante comunicação síncrona
- Clientes devem estar ativos para receber mensagens
- Falhas de conexão resultam em remoção automática do cliente

### Descoberta de Serviços
- Servidor central mantém registro de todos os clientes
- Clientes consultam servidor para encontrar outros clientes
- Conexão direta P2P após descoberta

### Tolerância a Falhas
- Remoção automática de clientes inativos
- Tratamento de exceções de rede
- Verificação de conectividade antes do envio

## Portas Utilizadas
- **1099**: RMI Registry do servidor central
- **1100+**: Portas individuais de cada cliente para P2P

## Arquivos de Teste
- `teste.txt`: Arquivo de exemplo para testar envio de arquivos

---
**Desenvolvido para demonstrar conceitos de RMI, arquitetura distribuída e comunicação P2P**
