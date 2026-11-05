package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.uvgspot.domain.model.Availability

@Composable
fun AvailabilityReportDialog(
    onDismiss: () -> Unit,
    onConfirm: (Availability) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text("Reportar disponibilidad") },
        text = {
            Column {
                Text("Selecciona lo que observas. Se enviará con una sesión anónima.")
                listOf(Availability.HIGH, Availability.MEDIUM, Availability.LOW).forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { selectedName = option.name }.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedName == option.name, onClick = { selectedName = option.name })
                        Text(when (option) {
                            Availability.HIGH -> "Alta disponibilidad"
                            Availability.MEDIUM -> "Disponibilidad media"
                            Availability.LOW -> "Baja disponibilidad"
                            Availability.UNKNOWN -> "Sin información reciente"
                        })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedName != null,
                onClick = { selectedName?.let { onConfirm(Availability.valueOf(it)) } }
            ) { Text("Enviar reporte") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
