package com.fasttrade.android.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.theme.*

private data class FaqItem(val question: String, val answer: String)

private val faqItems = listOf(
    FaqItem(
        "Como faço para comprar um produto?",
        "Navegue pela home ou use a busca para encontrar o produto desejado. Clique nele para ver os detalhes, escolha a quantidade e toque em 'Adicionar ao carrinho'. Finalize a compra no carrinho informando o endereço de entrega."
    ),
    FaqItem(
        "Como acompanho meu pedido?",
        "Acesse a aba 'Pedidos' no menu inferior. Lá você encontra todos os seus pedidos com o status atualizado em tempo real: Enviado, Confirmado, Em Separação, Transporte e Entregue."
    ),
    FaqItem(
        "Posso cancelar um pedido?",
        "Pedidos podem ser cancelados enquanto estiverem com status 'Pendente' ou 'Confirmado'. Acesse o detalhe do pedido e utilize a opção de cancelamento. Após o envio, é necessário aguardar a entrega e solicitar devolução."
    ),
    FaqItem(
        "Como funciona a devolução de produtos?",
        "Você tem 7 dias corridos após o recebimento para solicitar devolução. O produto deve estar em perfeito estado, com embalagem original. Entre em contato com o vendedor pelo chat ou via 'Fale Conosco'."
    ),
    FaqItem(
        "Quais formas de pagamento são aceitas?",
        "Atualmente aceitamos pagamento via PIX. Outras formas de pagamento como cartão de crédito e boleto estão em breve."
    ),
    FaqItem(
        "Como me torno um vendedor?",
        "Para vender no Fast Trade, você precisa de uma conta verificada. Entre em contato pelo 'Fale Conosco' para solicitar acesso de vendedor. Nossa equipe analisará seu pedido em até 48 horas."
    ),
    FaqItem(
        "Como entro em contato com um vendedor?",
        "Acesse a página do produto e toque no nome do vendedor para ver o perfil. Na tela do perfil, toque em 'Enviar mensagem' para iniciar uma conversa direta."
    ),
    FaqItem(
        "Meus dados estão seguros?",
        "Sim. Utilizamos criptografia de ponta a ponta e seguimos rigorosamente a LGPD (Lei Geral de Proteção de Dados). Seus dados nunca são vendidos a terceiros. Consulte nossa Política de Privacidade para mais detalhes."
    ),
    FaqItem(
        "Como altero minha senha?",
        "Acesse 'Meu Perfil' e toque em 'ALTERAR' ao lado do campo Senha. Você precisará informar a senha atual e definir a nova senha, que deve ter no mínimo 8 caracteres."
    ),
    FaqItem(
        "O que faço se não recebi o produto?",
        "Caso o prazo de entrega tenha passado, verifique o status do pedido. Se ainda aparecer 'Em Transporte', aguarde mais 2 dias úteis. Persistindo o problema, entre em contato pelo 'Fale Conosco' com o número do pedido."
    )
)

@Composable
fun FaqScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        FtTopBar(title = "Perguntas Frequentes", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Como podemos ajudar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            faqItems.forEachIndexed { index, item ->
                FaqCard(item = item)
            }

            Spacer(Modifier.height(24.dp))

            // Não encontrou resposta
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Não encontrou o que procurava?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "Nossa equipe de suporte está disponível para ajudar. Entre em contato pelo 'Fale Conosco' na tela de perfil.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (expanded) FontWeight.Bold else FontWeight.Medium,
                    color = if (expanded) Primary else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (expanded) Primary else TextHint,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 16.dp))
                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                    )
                }
            }
        }
    }
}
