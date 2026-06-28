package com.fasttrade.android.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.theme.TextPrimary
import com.fasttrade.android.ui.theme.TextSecondary

enum class StaticPage { ABOUT, TERMS, PRIVACY }

@Composable
fun StaticContentScreen(page: StaticPage, onBack: () -> Unit) {
    val (title, sections) = when (page) {
        StaticPage.ABOUT -> "Sobre o Fast Trade" to listOf(
            null to "O Fast Trade é um marketplace brasileiro focado em conectar compradores e vendedores de forma simples, segura e eficiente.",
            "Nossa missão" to "Democratizar o comércio eletrônico, permitindo que qualquer pessoa possa comprar e vender produtos com facilidade e confiança.",
            "Como funciona" to "Vendedores cadastram seus produtos na plataforma e compradores podem navegar, comparar preços e efetuar compras diretamente pelo aplicativo. Todo o processo de pagamento e entrega é acompanhado em tempo real.",
            "Segurança" to "Utilizamos criptografia de ponta a ponta para proteger seus dados. Todas as transações passam pelo nosso sistema antifraude e os pagamentos ficam retidos até a confirmação da entrega.",
            "Versão do aplicativo" to "Versão 1.0.0 • Build 2024.1\nDesenvolvido com ❤️ no Brasil"
        )

        StaticPage.TERMS -> "Termos de Uso" to listOf(
            null to "Última atualização: 1º de janeiro de 2024. Ao utilizar o Fast Trade, você concorda com os termos descritos abaixo.",
            "1. Aceitação dos termos" to "O uso do aplicativo Fast Trade implica a aceitação integral destes Termos de Uso. Caso não concorde com qualquer disposição, não utilize o serviço.",
            "2. Cadastro e conta" to "Para utilizar o Fast Trade é necessário criar uma conta com informações verídicas. Você é responsável pela confidencialidade da sua senha e por todas as atividades realizadas com sua conta.",
            "3. Publicação de produtos" to "Ao publicar um produto, o vendedor garante que possui o direito de venda do item, que as informações são precisas e que o produto não viola direitos de terceiros ou legislação vigente.",
            "4. Transações" to "O Fast Trade atua como intermediador entre compradores e vendedores. Os pagamentos são processados de forma segura e liberados ao vendedor após a confirmação da entrega pelo comprador.",
            "5. Cancelamentos e devoluções" to "O comprador tem até 7 dias após o recebimento para solicitar a devolução do produto, conforme o Código de Defesa do Consumidor. O vendedor deve aceitar devoluções por defeito ou divergência com o anúncio.",
            "6. Responsabilidades" to "O Fast Trade não se responsabiliza por danos decorrentes do uso indevido da plataforma, informações incorretas fornecidas por usuários ou problemas nas transações entre partes.",
            "7. Alterações" to "Estes termos podem ser alterados a qualquer momento. Notificaremos os usuários sobre mudanças significativas via e-mail ou notificação no aplicativo."
        )

        StaticPage.PRIVACY -> "Políticas de Privacidade" to listOf(
            null to "Última atualização: 1º de janeiro de 2024. Esta Política descreve como coletamos, usamos e protegemos suas informações pessoais.",
            "1. Dados coletados" to "Coletamos informações fornecidas no cadastro (nome, e-mail, CPF, telefone, endereço), dados de uso do aplicativo, histórico de transações e informações do dispositivo para fins de segurança.",
            "2. Uso das informações" to "Utilizamos seus dados para processar transações, personalizar sua experiência, enviar comunicações sobre pedidos, prevenir fraudes e cumprir obrigações legais.",
            "3. Compartilhamento" to "Seus dados não são vendidos a terceiros. Podemos compartilhar informações com processadores de pagamento, transportadoras e autoridades quando exigido por lei.",
            "4. Armazenamento e segurança" to "Seus dados são armazenados em servidores seguros com criptografia AES-256. Implementamos medidas técnicas e organizacionais para proteger contra acesso não autorizado.",
            "5. Seus direitos (LGPD)" to "Você tem direito a acessar, corrigir, excluir e exportar seus dados pessoais. Você pode revogar consentimentos a qualquer momento. Para exercer esses direitos, entre em contato via 'Fale Conosco'.",
            "6. Cookies e rastreamento" to "Utilizamos cookies e tecnologias similares para melhorar a experiência de navegação. Você pode gerenciar as preferências de cookies nas configurações do aplicativo.",
            "7. Contato" to "Para dúvidas sobre esta política, entre em contato pelo e-mail privacidade@fasttrade.com.br ou acesse a seção 'Fale Conosco' no aplicativo."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        FtTopBar(title = title, onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            sections.forEach { (heading, body) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (heading != null) {
                        Text(
                            text = heading,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (heading == null) TextPrimary else TextSecondary,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f
                    )
                }
                if (heading != null) HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
