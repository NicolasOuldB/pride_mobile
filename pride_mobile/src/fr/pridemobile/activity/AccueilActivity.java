package fr.pridemobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.pridemobile.R;
import fr.pridemobile.adapter.ProjetsListAdapter;
import fr.pridemobile.adapter.navigation.NavigationListAdapterDetailProjet;
import fr.pridemobile.application.PrideApplication;
import fr.pridemobile.application.PrideConfiguration;
import fr.pridemobile.model.ListeProjetsResponse;
import fr.pridemobile.model.NavigationDrawerElement;
import fr.pridemobile.model.beans.Projet;
import fr.pridemobile.service.WSCallable;
import fr.pridemobile.utils.Constants;

public class AccueilActivity extends PrideAbstractActivity {

	private static final String TAG = "ACCUEIL";

	/** El�ments de l'interface */
	private ListView projetsListeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_accueil);
		List<NavigationDrawerElement> drawerData = new ArrayList<NavigationDrawerElement>();
		drawerData.add(new NavigationDrawerElement("Home"));
		drawerData.add(new NavigationDrawerElement("Autre"));
		drawerData.add(new NavigationDrawerElement("Les collaborateurs"));
		nitView(new NavigationListAdapterDetailProjet(this, drawerData));
		if (toolbar != null) {
            toolbar.setTitle("Accueil");
            setSupportActionBar(toolbar);
        }
	    initDrawer();

		// �l�ments
		projetsListeView = (ListView) findViewById(R.id.liste_projets);
		getProjets();
	}
	
	private void initProjetList() {
		TextView txtlisteVide = (TextView) findViewById(R.id.listeProjetsVide);
		ArrayAdapter<Projet> adapter = new ProjetsListAdapter(AccueilActivity.this, PrideApplication.INSTANCE.getUtilisateurProjets());
		projetsListeView.setAdapter(adapter);
		projetsListeView.setEmptyView(txtlisteVide);
	}

	private void getProjets() {
		Log.i(TAG, "Tentative de r�cup�ration des projets");

		String login = prefs.getString(Constants.PREF_LOGIN, null);
		
		// Construction de l'URL
		String url = PrideApplication.INSTANCE.getProperties(PrideConfiguration.WS_UTILISATEURS,
				PrideConfiguration.WS_UTILISATEURS_PROJET);
		url += "/"+login;
		callWSGet(url, ListeProjetsResponse.class, new WSCallable<ListeProjetsResponse>() {

			@Override
			public Void call() throws Exception {
				String errorCode = response.getCode();
				if (response.isSuccess()) {
					List<Projet> projets = response.getData();
					PrideApplication.INSTANCE.setUtilisateurProjets(projets);
					showToastFromThread("Les r�sultats des data : " + projets, AccueilActivity.this);
					initProjetList();
				} else {
					// Erreur inconnue
					logError(TAG, response);
					showErrorFromCode(errorCode);
				}
				return null;
			}
		});
	}
}
