package com.example.listadetarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Roxo = Color(0xFF6D5CE8)
private val Fundo = Color(0xFFF7F6FC)

// Representa os dados de cada tarefa cadastrada.
private data class Tarefa(
    val id: Int,                 // Identificador único da tarefa.
    val titulo: String,          // Nome da tarefa.
    val descricao: String,       // Descrição da tarefa.
    val concluida: Boolean = false // Indica se foi concluída.
)

// Classe responsável pela tela principal do aplicativo.
class MainActivity : ComponentActivity() {

    // Método executado quando a atividade é criada.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define a interface do aplicativo usando Jetpack Compose.
        setContent {

            // Configura o tema e a cor principal.
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Roxo
                )
            ) {
                // Chama a função que desenha a lista de tarefas.
                ListaDeTarefas()
            }
        }
    }
}

// Função que constrói toda a interface da lista de tarefas.
@Composable
private fun ListaDeTarefas() {

    // Guarda o texto digitado no campo do título.
    var titulo by remember { mutableStateOf("") }

    // Guarda o texto digitado no campo da descrição.
    var descricao by remember { mutableStateOf("") }

    // Controla o identificador da próxima tarefa.
    var proximoId by remember { mutableStateOf(0) }

    // Cria uma lista observável que atualiza a tela quando muda.
    val tarefas = remember { mutableStateListOf<Tarefa>() }

    // Controla as mensagens temporárias exibidas na tela.
    val snackbarHostState = remember { SnackbarHostState() }

    // Permite executar as mensagens usando corrotinas.
    val scope = rememberCoroutineScope()

    // Conta quantas tarefas foram concluídas.
    val concluidas = tarefas.count { it.concluida }

    // Calcula quantas tarefas ainda estão pendentes.
    val pendentes = tarefas.size - concluidas

    // Organiza a tela e disponibiliza a área das mensagens.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Fundo,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        // Column organiza os componentes verticalmente.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Row organiza o ícone e os textos lado a lado.
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Box cria o espaço reservado para o ícone.
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Roxo,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Símbolo que representa uma tarefa concluída.
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 30.sp
                    )
                }

                // Cria um espaço entre o ícone e o título.
                Spacer(modifier = Modifier.width(12.dp))

                // Agrupa o título e o texto informativo.
                Column {
                    Text(
                        text = "Minhas tarefas",
                        fontSize = 26.sp,
                        color = Roxo
                    )

                    Text(text = "Organize suas tarefas do dia!")
                }
            }

            // Primeiro campo de entrada: título da tarefa.
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título da tarefa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Segundo campo de entrada: descrição da tarefa.
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição da tarefa") },
                modifier = Modifier.fillMaxWidth()
            )


            // Botão principal para cadastrar uma nova tarefa.
            Button(
                onClick = {

                    // Impede o cadastro se o título estiver vazio.
                    if (titulo.isBlank()) {

                        // Exibe uma mensagem solicitando o título.
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Digite o título da tarefa!"
                            )
                        }

                    } else {

                        // Adiciona a nova tarefa à lista.
                        tarefas.add(
                            Tarefa(
                                id = proximoId++,
                                titulo = titulo.trim(),
                                descricao = descricao.trim()
                            )
                        )

                        // Limpa os campos após o cadastro.
                        titulo = ""
                        descricao = ""

                        // Informa que a tarefa foi adicionada.
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Tarefa adicionada!"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Adicionar tarefa")
            }

            // Card apresenta um resumo das tarefas cadastradas.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                // Organiza as informações do resumo verticalmente.
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resumo das tarefas",
                        fontSize = 18.sp,
                        color = Roxo
                    )

                    // Mostra o total de tarefas.
                    Text(text = "Total: ${tarefas.size}")

                    // Mostra quantas ainda estão pendentes.
                    Text(text = "Pendentes: $pendentes")

                    // Mostra quantas foram concluídas.
                    Text(text = "Concluídas: $concluidas")
                }
            }

            // Título da seção que exibe as tarefas.
            Text(
                text = "Minha lista",
                fontSize = 20.sp,
                color = Roxo
            )

            // Verifica se ainda não existem tarefas cadastradas.
            if (tarefas.isEmpty()) {

                // Exibe uma mensagem quando a lista está vazia.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma tarefa cadastrada.\nAdicione sua primeira tarefa!",
                        color = Color.DarkGray
                    )
                }

            } else {

                // LazyColumn exibe os itens com rolagem vertical.
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Cria um item visual para cada tarefa cadastrada.
                    items(
                        items = tarefas,
                        key = { tarefa -> tarefa.id }
                    ) { tarefa ->

                        // Card separa visualmente cada tarefa.
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            // Organiza o checkbox e os textos na horizontal.
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Checkbox permite marcar a tarefa como concluída.
                                Checkbox(
                                    checked = tarefa.concluida,
                                    onCheckedChange = { marcada ->

                                        // Localiza a tarefa pelo seu identificador.
                                        val indice = tarefas.indexOfFirst {
                                            it.id == tarefa.id
                                        }

                                        // Atualiza o estado da tarefa selecionada.
                                        if (indice != -1) {
                                            tarefas[indice] = tarefa.copy(
                                                concluida = marcada
                                            )
                                        }

                                        // Exibe uma mensagem conforme a alteração.
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (marcada) {
                                                    "Tarefa concluída!"
                                                } else {
                                                    "Tarefa pendente!"
                                                }
                                            )
                                        }
                                    }
                                )

                                // Separa o checkbox dos textos.
                                Spacer(modifier = Modifier.width(8.dp))

                                // Organiza título, descrição e status.
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    // Mostra o título da tarefa.
                                    Text(
                                        text = tarefa.titulo,
                                        fontSize = 16.sp,
                                        color = if (tarefa.concluida) {
                                            Color.Gray
                                        } else {
                                            Color.Black
                                        }
                                    )

                                    // Só mostra a descrição se ela não estiver vazia.
                                    if (tarefa.descricao.isNotBlank()) {
                                        Spacer(
                                            modifier = Modifier.height(4.dp)
                                        )

                                        Text(text = tarefa.descricao)
                                    }

                                    // Informa se a tarefa está pendente ou concluída.
                                    Text(
                                        text = if (tarefa.concluida) {
                                            "Concluída"
                                        } else {
                                            "Pendente"
                                        },
                                        fontSize = 12.sp,
                                        color = Roxo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}