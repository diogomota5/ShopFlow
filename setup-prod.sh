#!/usr/bin/env bash
# =============================================================
# setup-prod.sh — Script de configuração inicial do ShopFlow
# Execute uma vez antes do primeiro deploy:
#   chmod +x setup-prod.sh && ./setup-prod.sh
# =============================================================

set -e

echo "=== ShopFlow — Setup de Produção ==="
echo ""

# 1. Verificar se o arquivo de credenciais existe
if [ ! -f "Credenciais.env" ]; then
  echo "[INFO] Copiando Credenciais.env.example → Credenciais.env"
  cp Credenciais.env.example Credenciais.env
else
  echo "[OK] Credenciais.env já existe."
fi

# 2. Gerar JWT_SECRET forte automaticamente (requer openssl)
if command -v openssl &> /dev/null; then
  JWT=$(openssl rand -hex 64)
  if grep -q "^JWT_SECRET=$" Credenciais.env; then
    sed -i "s|^JWT_SECRET=$|JWT_SECRET=$JWT|" Credenciais.env
    echo "[OK] JWT_SECRET gerado automaticamente."
  else
    echo "[SKIP] JWT_SECRET já está preenchido."
  fi
else
  echo "[WARN] openssl não encontrado. Preencha JWT_SECRET manualmente em Credenciais.env"
fi

echo ""
echo "=== Próximos passos ==="
echo "1. Edite Credenciais.env e preencha:"
echo "   - DB_USERNAME / DB_PASSWORD"
echo "   - CLOUDINARY_NAME / CLOUDINARY_KEY / CLOUDINARY_SECRET"
echo "   - MAIL_HOST / MAIL_USERNAME / MAIL_PASSWORD"
echo "   - FRONTEND_URL=https://seudominio.com"
echo "   - ALLOWED_ORIGINS=https://seudominio.com"
echo ""
echo "2. Execute o deploy com:"
echo "   docker compose -f docker-compose.prod.yml --env-file Credenciais.env up -d --build"
echo ""
echo "=== Concluído ==="
