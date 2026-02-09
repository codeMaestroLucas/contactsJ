package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.to_test.*;
import org.example.src.utils.ContinentConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for ByNewPage firms.
 * Constructs the list of firms based on enabled continents from continentsConfig.json.
 */
public class ByNewPageFirmsBuilder {

    private static final Site[] AFRICA = {
            new ENSAfrica(), new HansOffiaAndAssociates(), new JacksonEttiAndEdu(), new Shalakany(), new Werksmans(),
            new ZakiHashemAndPartners(), new ZulficarAndPartners()
    };

    private static final Site[] ASIA = {
            new AOil(), new BarneaAndCo(), new CovenantChambers(), new DSKLegal(), new EBN(),
            new Fischer(), new GornitzkyAndCo(), new HuiyeLaw(), new HYLeungAndCo(), new JSA(),
            new KRBLaw(), new LonganLaw(), new MishconKaras(), new Mourant(), new NurmansyahAndMuzdalifah(),
            new RemfryAndSagar(), new SamvadPartners(), new TannerDeWitt(), new TCLaw(),
            new CityYuwaPartners(), new DesaiAndDiwanji(), new HarryElias(), new MiuraAndPartners(),
    };

    private static final Site[] EUROPE = {
            new ABGIP(), new ACAndR(), new Astrea(), new Avance(), new BadriAndSalimElMeouchiLaw(),
            new Beauchamps(), new Belgravia(), new Borenius(), new Boyanov(), new BRAUNEISRECHTSANWALTE(),
            new BurgesSalmon(), new CarneluttiLaw(), new CRCCD(), new DANUBIAPatentAndLaw(), new DGKV(),
            new DKGV(), new Dottir(), new EisenfuhrSpeiserAndPartner(), new Ekelmans(), new EllisonsSolicitors(),
            new EPAndC(), new FCMLimited(), new FPSLaw(), new FrancisWilksAndJones(), new Frontier(),
            new Fylgia(), new GittiAndPartners(), new Goerg(), new GORG(), new GreeneAndGreeneSolicitors(),
            new Hamso(), new HarteBavendamm(), new HaslingerNagele(), new HoffmannEitle(), new Holmes(),
            new Horten(), new JadekAndPensa(), new JBLaw(), new JWP(), new KallioLaw(),
            new KambourovAndPartners(), new KBVLLaw(), new KennedyVanderLaan(), new KeystoneLaw(), new KLCLaw(),
            new Kondrat(), new Kvale(), new KWKRLaw(), new LambadariosLaw(), new LangsethAdvokat(),
            new LePooleBekema(), new Legalis(), new Lindahl(), new MaikowskiAndNinnemann(), new MeyerKoring(),
            new MorrisLaw(), new MSBSolicitors(), new NESTOR(), new OgletreeDeakins(), new OneEssexCourt(),
            new Onsagers(), new ONVLaw(), new Oppenheim(), new Oxera(), new PhilippeAndPartners(),
            new PinneyTalfourdSolicitors(), new PMP(), new PortaAndConsulentiAssociati(), new Poulschmith(), new PrinzAndPartner(),
            new PrueferAndPartner(), new PuschWahlig(), new Racine(), new ReinhardSkuhraWeiseAndPartnerGbR(), new RonanDalyJermyn(),
            new RymarzZdortMaruta(), new SampsonCowardLLP(), new SBGK(), new Schoups(), new SKWSchwarz(),
            new SlaughterAndMay(), new SRSLegal(), new Strelia(), new SZA(), new Szecskay(),
            new Valfor(), new VanOlmenAndWynant(), new Vischer(), new VossiusAndPartner(), new WardynskiAndPartners(),
            new Wiersholm(), new WikborgRein(), new ZeposAndYannopoulos(), new GVZH()
    };

    private static final Site[] NORTH_AMERICA = {
            new ArthurCox(), new AsafoAndCo(), new BCFLaw(), new BWBLLP(), new FilionWakelyThorupAngeletti(),
            new IbanezParkman(), new KuriBrena(), new MBM(), new NaderHayauxAndGoebel(), new PrasadAndCompany(),
            new Sangra(), new ThompsonDorfmanSweatman(), new VazquezTerceroAndZepeda(), new McKinneyBancroftAndHughes(),
    };

    private static final Site[] CENTRAL_AMERICA = {
            new MyersFletcherAndGordon()
    };

    private static final Site[] SOUTH_AMERICA = {
            new BrigrardUrrutia(), new CariolaDiezPerezCotapos(), new Ferrere(), new LatinAlliance(), new Madrona(),
            new RMADVAdvogados(), new SargentAndKrahn(), new ZBV(), new Carey(), new GaiaSilvaGaedeAndAssociados()
    };

    private static final Site[] OCEANIA = {
            new Baumgartners(), new DeutschMiller(), new DWFoxTucker(), new Gadens(), new SimmonsWolfhagen(),
            new TompkinsWake(), new YoungList(), new Grette(), new RussellMcVeagh(), new WilsonHarle(),
    };

    private static final Site[] MUNDIAL = {
            new ALGoodbody(), new BDO(), new CerhaHempel(), new Cobalt(), new ControlRisks(),
            new Cuatrecasas(), new Curtis(), new Dentons(), new EProint(), new Ellex(),
            new FangdaPartners(), new GuantaoLaw(), new HiggsAndJohnson(), new Houthoof(), new JohnsonCamachoAndSingh(),
            new JPMAndPartners(), new LatamLex(), new Legance(), new LexCaribbean(), new LewissSilkin(),
            new MarksAndClerk(), new MdME(), new METIDA(), new Noerr(), new OsborneClarke(),
            new PearlCohen(), new PortolanoCavallo(), new Pulegal(), new QuinEmanuel(), new SabaAndCo(),
            new SimmonsAndSimmons(), new Sorainen(), new TEMPLARS(), new Thommessen(), new Vaneps(),
            new WALLESS(),


            // ByPage - Asia
            new GuoyaoQindaoLaw(),

// ByPage - Mundial
            new HadefAndPartners(),

// ByPage - Oceania
            new HarmosHortonLusk(),

// ByNewPage - Asia
            new FoongAndPartners(), new HarryEliasPartnership(),
    };

    // ==================== GETTERS BY CONTINENT ====================

    public static Site[] getAfrica() { return AFRICA; }
    public static Site[] getAsia() { return ASIA; }
    public static Site[] getEurope() { return EUROPE; }
    public static Site[] getNorthAmerica() { return NORTH_AMERICA; }
    public static Site[] getCentralAmerica() { return CENTRAL_AMERICA; }
    public static Site[] getSouthAmerica() { return SOUTH_AMERICA; }
    public static Site[] getOceania() { return OCEANIA; }
    public static Site[] getMundial() { return MUNDIAL; }

    // ==================== BUILD METHOD ====================

    /**
     * Builds the list of ByNewPage firms based on enabled continents.
     * Mundial firms are always included (global firms).
     *
     * @return Array of Site objects for enabled continents
     */
    public static Site[] build() {
        List<Site> sites = new ArrayList<>();

        if (ContinentConfig.isContinentEnabled("Africa")) sites.addAll(Arrays.asList(AFRICA));
        if (ContinentConfig.isContinentEnabled("Asia")) sites.addAll(Arrays.asList(ASIA));
        if (ContinentConfig.isContinentEnabled("Europe")) sites.addAll(Arrays.asList(EUROPE));
        if (ContinentConfig.isContinentEnabled("North America")) sites.addAll(Arrays.asList(NORTH_AMERICA));
        if (ContinentConfig.isContinentEnabled("Central America")) sites.addAll(Arrays.asList(CENTRAL_AMERICA));
        if (ContinentConfig.isContinentEnabled("South America")) sites.addAll(Arrays.asList(SOUTH_AMERICA));
        if (ContinentConfig.isContinentEnabled("Oceania")) sites.addAll(Arrays.asList(OCEANIA));

        // Mundial is always included (global firms)
        sites.addAll(Arrays.asList(MUNDIAL));

        return sites.toArray(new Site[0]);
    }
}
