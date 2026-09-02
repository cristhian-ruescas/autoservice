import http from 'k6/http';
import { sleep, check } from 'k6';

// Configuração dos estágios de carga simulando o pico de uso da oficina
export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Ramp-up: sobe para 20 usuários simultâneos em 30s
    { duration: '2m', target: 150 },  // Pico: mantém 150 usuários por 2 minutos
    { duration: '30s', target: 0 },   // Ramp-down: esfria o sistema zerando os usuários
  ],
};

export default function () {
  // URL do endpoint de teste de métricas
  const url = 'http://localhost:8088/ordens-servico/metricas/tempo-medio-execucao';
  
  const res = http.get(url);
  
  // Valida se a requisição deu sucesso (Status 200)
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  
  // Pausa de 1 segundo entre as requisições de cada usuário virtual
  sleep(1);
}