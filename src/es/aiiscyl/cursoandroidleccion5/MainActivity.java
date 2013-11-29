package es.aiiscyl.cursoandroidleccion5;

import java.io.File;

import es.aiiscyl.cursoandroidleccion5.service.DownloadService;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView textView;
	private EditText urlText;
	private EditText nombreFicheroDestinoText;
	private DownloadManager dm;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String string = bundle.getString(DownloadService.FILEPATH);
				int resultCode = bundle.getInt(DownloadService.RESULT);
				if (resultCode == RESULT_OK) {
					Toast.makeText(MainActivity.this,
							"Descarga completa. Fichero en URI: " + string,
							Toast.LENGTH_LONG).show();
					textView.setText("Descarga completa");
				} else {
					Toast.makeText(MainActivity.this, "Descarga fallida",
							Toast.LENGTH_LONG).show();
					textView.setText("Descarga fallida");
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.status);
		urlText = (EditText) findViewById(R.id.downloadurl);
		nombreFicheroDestinoText = (EditText) findViewById(R.id.nombreFicheroDestino);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	public void descargarServicioPropio(View view) {
		String urlDescargar = urlText.getText().toString();
		String nombreFicheroDestino = nombreFicheroDestinoText.getText().toString();
		if (!TextUtils.isEmpty(urlDescargar)&&(!TextUtils.isEmpty(nombreFicheroDestino))) {
			Intent intent = new Intent(this, DownloadService.class);
			// Añade información para el servicio sobre el fichero a descargar y nombre del fichero que se guardará.
			intent.putExtra(DownloadService.FILENAME, nombreFicheroDestino);
			intent.putExtra(DownloadService.URL, urlDescargar);
			startService(intent);
			textView.setText("Servicio iniciado");			
		}
	}

	public void descargaDownloadManager(View view) {
		String urlDescargar = urlText.getText().toString();
		Uri ficheroDestino = getSdCardUri();
		if (!TextUtils.isEmpty(urlDescargar)&&(ficheroDestino!=null)) {
			dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			Request request = new Request(Uri.parse(urlDescargar));	
			request.setDestinationUri(ficheroDestino);
			dm.enqueue(request);
		}
	}
	
	private Uri getSdCardUri() {
		String nombreFicheroDestino = nombreFicheroDestinoText.getText().toString();
        String rootUri = Environment.getExternalStorageDirectory().getPath() + "/";
        Uri uri = null;
        if (!TextUtils.isEmpty(nombreFicheroDestino)) {
        	final File file = new File(rootUri,nombreFicheroDestino);
        	uri = Uri.fromFile(file);
        }
        return uri;
    }	
}
