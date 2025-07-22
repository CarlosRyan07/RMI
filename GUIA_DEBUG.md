# Guia de Teste - Debug do Chat RMI

## Problemas Identificados e Correções

### Problema 1: Mensagens privadas não encontram clientes
### Problema 2: Clientes sendo removidos incorretamente

## Melhorias Implementadas

### 1. Debug Detalhado
- Servidor mostra clientes cadastrados ao buscar
- Cliente mostra URLs de conexão
- Auto-teste de conectividade
- Logs detalhados de erros

### 2. Comando /debug
- Mostra informações do cliente atual
- Testa auto-conectividade
- Verifica conexão com servidor

### 3. Logs Melhorados
- URLs de conexão P2P
- Estado dos clientes cadastrados
- Detalhes de erros de rede

## Como Testar - Passo a Passo

### 1. Inicie o Servidor
```bash
java ServidorChat
```

### 2. Abra Cliente 1
```bash
java ClienteChat
```
- Digite: `joao` (sem @)
- Porta: `1100`
- Servidor: Enter (localhost)

### 3. Teste o Cliente 1
```
>> /debug
>> /listar
```

### 4. Abra Cliente 2 (novo terminal)
```bash
java ClienteChat
```
- Digite: `maria` (sem @)
- Porta: `1101`
- Servidor: Enter (localhost)

### 5. Teste o Cliente 2
```
>> /debug
>> /listar
```

### 6. Teste Mensagem Privada
No Cliente 1:
```
>> /msg maria Oi Maria!
```

No Cliente 2:
```
>> /msg joao Oi João!
```

### 7. Teste Mensagem de Sala
No Cliente 1:
```
>> /sala Olá pessoal!
```

### 8. Verificar no Servidor
```
servidor> listar
```

## O que Observar

### Sinais de Sucesso:
- ✓ Auto-teste: FUNCIONANDO
- ✓ Cliente encontrado no servidor
- ✓ Mensagem enviada
- ✓ Clientes permanecem na lista

### Sinais de Problema:
- ✗ Auto-teste: FALHA
- ✗ Cliente não encontrado
- ✗ Erro de conexão RMI
- ✗ Clientes sendo removidos

## Possíveis Causas e Soluções

### 1. Problema de Firewall/Rede
**Sintoma:** Auto-teste falha
**Solução:** Verificar firewall, usar IP 127.0.0.1

### 2. Conflito de Portas
**Sintoma:** Erro ao criar registry
**Solução:** Usar portas diferentes (1100, 1101, 1102...)

### 3. Problema de Registro RMI
**Sintoma:** NotBoundException
**Solução:** Verificar se o nome do serviço está correto

### 4. Problema de IP
**Sintoma:** Conexão falha entre clientes
**Solução:** Todos os clientes devem usar o mesmo IP

## Comandos de Debug

### No Cliente:
- `/debug` - Informações do cliente
- `/listar` - Clientes ativos no servidor

### No Servidor:
- `listar` - Mostrar todos os clientes
- `limpar` - Verificar conectividade

## Log Esperado (Sucesso)

### Servidor:
```
Cliente registrado: @joao (192.168.1.100:1100)
Cliente registrado: @maria (192.168.1.100:1101)
Buscando cliente: @maria
Clientes cadastrados: [joao, maria]
✓ Cliente encontrado: @maria (192.168.1.100:1101)
```

### Cliente:
```
✓ Servidor P2P iniciado:
  - IP: 192.168.1.100
  - Porta: 1100
  - Serviço: Cliente_joao
✓ Auto-teste bem-sucedido: true
✓ Conectado ao servidor de chat como @joao
```

Execute este teste e me informe os resultados!
