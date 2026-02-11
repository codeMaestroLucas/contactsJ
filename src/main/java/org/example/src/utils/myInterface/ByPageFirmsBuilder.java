package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.HHRLawyers;
import org.example.src.sites.byNewPage.SolomonAndCo;
import org.example.src.sites.byPage.*;
import org.example.src.utils.ContinentConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for ByPage firms.
 * Constructs the list of firms based on enabled continents from continentsConfig.json.
 */
public class ByPageFirmsBuilder {

    private static final Site[] AFRICA = {
            new Adsero(), new ALPNGAndCo(), new AlukoAndOyebode(), new AmanAndPartners(), new Ashitiva(),
            new BentsiEnchillLetsaAndAnkomah(), new FisherQuarmbyAndPfeifer(), new KantorAndImmerman(), new ShahidLaw(), new STBB(),
            new TheartMey()
    };

    private static final Site[] ASIA = {
            new ABNR(), new Aequitas(), new AllBrightLaw(), new AllenAndGledhill(), new ALMTLegal(),
            new AMTLaw(), new AnandAndAnand(), new AquinasLawAlliance(), new AronTadmorLevy(), new AssegafHamzahAndPartners(),
            new BaeKimAndLee(), new BraunPartners(), new BSALaw(), new CFNLaw(), new Deacons(),
            new DRAndAJU(), new FironLaw(), new FoxAndMandal(), new GrandwayLaw(), new Helmsman(),
            new HFAndCo(), new HowseWilliams(), new K1Chamber(), new KECOLegal(), new LeeAndKo(),
            new MASLaw(), new MeitarLaw(), new MorogluArseven(), new MZMLegal(), new OldhamLiAndNie(),
            new Paksoy(), new RamdasAndWong(), new SEUM(), new SFKSLaw(), new SHorowitzAndCo(),
            new ShinAndKim(), new SteinmetzHaringGurman(), new TiruchelvamAssociates(), new VeritasLegal(), new ZhongziLaw(),
            new ChandlerMHM(), new DrewAndNapier(), new EastAndConcord(), new FJAndGDeSaram(), new GulapaLaw(),
            new HiswaraBunjaminAndTandjung(), new Hiways(), new HugillAndIp(), new JinchengTongdaAndNeal(),
            new KhaitanAndCo(), new MomoOMatsuoAndNamba(), new MoriHamadaAndMatsumoto(), new NagashimaOhnoAndTsunematsu(), new NishimuraAndAsahi(),
            new OhEbashiLPCAndPartners(), new RahmatLimAndPartners(), new RomuloLawFirm(), new StevensonWongAndCo(),
            new IndiaLawOffices(), new JiaYuanLaw(), new KochharAndCo(), new Lektou(), new SagaLegal(),
            new TianYuan(), new ABSAndCo(), new TMIAssociates(), new Trilegal(), new TsarAndTsai(),
            new Virtus(), new ZaidIbrahimAndCo(), new SokSiphanaAndAssociates(),
            new ASAndHCliffordChance(), new BharuchaAndPartners(), new GuoyaoQindaoLaw(), new JuslawsAndConsult(), new KojimaLaw(),
            new ADCOLaw(), new BTGAdvaya(), new JeffLeongPoonAndWong(),
            new AtsumiAndSakai(), new BhandariNaqviRiaz(), new MakesAndPartners(), new DaiichiFuyo(), new DhavalVussonjiAndAssociates(),
            new FirstLawPC(), new JTJBInternationalLawyers(), new LNT(), new MohsinTayebalyAndCo(), new VellaniAndVellani(),
            new VisionAndAssociates(), new WongPartnership(), new Yulchon(),
    };

    private static final Site[] EUROPE = {
            new Aera(), new AGPAdvokater(), new Alliotts(), new AlstonAndBirdLLP(), new Amorys(),
            new AraozAndRueda(), new ArnesenIP(), new ArnoldAndSiedsma(), new ASCHukuk(), new BAHR(),
            new BancilaDiaconuSiAsociatii(), new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new Berggren(), new BlakeMorgan(),
            new BlandyAndBlandy(), new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(), new Broseta(),
            new BrownRudnick(), new BSJP(), new BullAndCo(), new BUREN(), new BureauPlattner(),
            new BussMurtonLaw(), new BYRO(), new ByrneWallace(), new CampbellsLegal(), new CastrenAndSnellman(),
            new CBA(), new Cirio(), new Clarkslegal(), new ClemensLaw(), new CLPLaw(),
            new Codex(), new CollasCrill(), new Contrast(), new CWAAssociates(), new DahlLaw(),
            new DavisPolkAndWardwell(), new DeClercq(), new DechertLLP(), new Delcade(), new DimitrovPetrovAndCo(),
            new DinovaRusevAndPartners(), new DMSLegal(), new Dompatent(), new DrzewieckiTomaszek(),
            new DZPLaw(), new EldibAdvocates(), new Elverdam(), new Esche(), new Eubelius(),
            new Fidal(), new FilipAndCompany(), new Finreg360(), new FIVERS(), new FlichyGrange(),
            new FluegelPreissner(), new Foyen(), new FranklinLaw(), new GanadoAdvocates(), new GorrissenFederspiel(),
            new GPK(), new GrataInternational(), new GreenHorseLegal(), new Haavind(),
            new HabrakenRutten(), new HammarskioldAndCo(), new HannesSnellman(), new HarperJamesSolicitors(), new Hayes(),
            new HCRLegal(), new Hellstrom(), new Hellstrom(), new HjulmandCaptain(), new Holst(),
            new HPPAttorneys(), new Hugel(), new Jalsovszky(), new Jalsovszky(), new JGSA(),
            new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new Kallan(), new Kanter(), new KewLaw(),
            new KienhuisLegal(), new Knijff(), new Knijff(), new KnezovicAndAssociates(), new KochanskiAndPartners(),
            new Kolster(), new Krogerus(), new KromannReumert(), new LaszczukAndWspolnicy(), new LemstraVanDerKorst(),
            new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Logos(), new LPAGGV(),
            new Lydian(), new MAQS(), new Matheson(), new MazantiAndersen(), new MccannFitzGerald(),
            new McDermottWillAndEmery(), new McDermottWillAndEmery(), new MellingVoitishkinAndPartners(), new MerilampiAttorneys(), new MitelAndAsociatii(),
            new MoalemWeitemeyer(), new Molinari(), new MooreLegalKovacs(), new MSP(), new MVJMarkovicVukoticJovkovic(),
            new MVVPAdvocaten(), new NielsenNorager(), new Njord(), new NOEWE(), new Norens(),
            new NovaLaw(), new NPPLegal(), new NunzianteMagrone(), new NysinghAdvocatenNotarissenNV(), new Odigo(),
            new Orrick(), new PanettaConsultingGroup(), new PayetReyCauviPerez(), new Pedersoli(), new PelsRijcken(),
            new Penta(), new PFPLaw(), new Ploum(), new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(),
            new PricaAndPartners(), new ProskauerRose(), new RadulescuAndMusoi(), new RBK(), new RocaJunyent(),
            new RoedlAndPartner(), new Roschier(), new RPCLegal(), new SchindlerAttorneys(), new SchurtiPartners(),
            new SelihAndPartnerji(), new Selmer(), new Sidley(), new SIRIUS(), new Stibbe(),
            new StoneKing(), new SuarezDeVivero(), new ThomasBodstrom(), new Titov(), new TucaZbarcea(),
            new VanDerPutt(), new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(),
            new WolfTheiss(), new ZamfirescuRacotiPredoiu()
    };

    private static final Site[] NORTH_AMERICA = {
            new BarristonLaw(), new BennettJones(), new BLGLaw(), new BurnetDuckworthAndPalmer(), new Cassels(),
            new ClarkWilson(), new DaleAndLessmann(), new DeethWilliamsWall(), new DillonEustace(), new ECLegalRubio(),
            new FillmoreRiley(), new FoglerRubinoff(), new Goodmans(), new HNA(), new Langlois(),
            new LawsonLundell(), new LEGlobal(), new LoopstraNixon(), new McDougallGauley(), new McKercher(),
            new MijaresAngoitiaCortesAndFuentes(), new MLTAikins(), new NautaDutilh(), new NelliganLaw(), new OslerHoskinAndHarcourt(),
            new OyenWiggs(), new RitchMueller(), new RitchMuellerAndNicolau(), new SantamarinaAndSteta(), new SmartAndBiggar(),
            new StikemanElliott(), new VillarrealVGF(), new WildeboerDellelce(), new CozenOConnor(), new UlisesCabrera(),
            new GrahamThompson(), new HeadrickRizikAlvarezAndFernandez()
    };

    private static final Site[] CENTRAL_AMERICA = {
            new GarciaBodan(), new McConnellValdes(),
    };

    private static final Site[] SOUTH_AMERICA = {
            new AguayoEcclefieldAndMartinez(), new AlvarezAbogados(), new Andersen(), new BarriosAndFuentes(), new BeccarVarela(),
            new Bermudes(), new BullrichFlanzbaum(), new CARAdvogados(), new CEPDAbogados(), new ChevezRuizZamarripa(),
            new FarrocoAbreuGuarnieriZotelli(), new HernandezAndCia(), new LEFOSSE(), new MUC(), new NelsonWiliansAndAdvogados(),
            new PayetReyCauviPerez(), new PayetReyCauviPerez(), new PPOAbogados(), new RennoPenteadoSampaioAdvogados(), new RobortellaEPeres(),
            new Tavares(), new AbeledoGottheil(), new AllendeAndBrea(), new BaptistaLuz(), new BarrosAndErrazuriz(),
            new Bocater(), new BrasilSalomaoeMatthes(), new BSVV(), new BustamanteFabara(),
            new EsguerraJHR(), new Felsberg(), new FLH(), new GSALegal(), new LacazMartinsPereiraNetoGurevichAndSchoueri(),
            new MachadoMeyer(), new MattosEngelbergEchenique(), new MENPA(), new MontauryPimentaMachadoAndVieiraDeMello(),
            new MQMGLD(), new MunizLaw(), new OlarteMoure(), new SerranoMartinezCMA(), new TauilAndChequer(),
            new TraviesoEvansArriaAndRengel(), new Tumnet(), new Vouga(), new WaldAntunesVitaEBlattner(),
    };

    private static final Site[] OCEANIA = {
            new AiGroup(), new AndersonLloyd(), new AnthonyHarper(), new BankiHaddockFiora(), new BuddleFindlay(),
            new DBHLaw(), new DuncanCotterill(), new JamesAndWells(), new MacphersonKelley(), new MalleyAndCo(),
            new MBIP(), new MinterEllisonRuddWatts(), new CarterNewell(), new Corrs(),
            new JohnsonWinterSlattery(), new LaneNeave(), new HoldingRedlich(), new PiperAlderman(), new ThomsonGeer(),
            new ArnoldBlochLeibler(), new HarmosHortonLusk(), new ClaytonUtz(), new GordonLegal(),
            new WynnWilliams(),
    };

    private static final Site[] MUNDIAL = {
            new ABAndDavid(), new AddleshawGoddardLLP(), new AlTamimi(), new Allens(), new ApplebyGlobal(),
            new ArnoldAndPorter(), new Ashurst(), new AVMAdvogados(), new AWA(), new BCLPLaw(),
            new Blakes(), new BNT(), new CareyOlsen(), new CassidyLevyKent(), new Chattertons(),
            new ClearyGottlieb(), new CliffordChance(), new Conyers(), new CovingtonAndBurlingLLP(), new CrowellAndMoring(),
            new DCCLaw(), new DebevoiseAndPlimpton(), new DuaneMorris(), new FaegreDrinkerBiddleAndReath(), new FRA(),
            new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodwin(), new GreenbergTraurig(), new HakunLaw(),
            new HavelPartners(), new HaynesAndBoone(), new HBNLaw(), new HerbertSmithFreehillsKramer(), new HFW(),
            new HillDickinson(), new HollandAndKnight(), new HuntonAndrewsKurth(), new JonesDay(), new JSKAdvokatni(),
            new Kinstellar(), new KISCHIP(), new LathamAndWatkins(), new MagnussonLaw(), new MayerBrown(),
            new McCarthyTetrault(), new Milbank(), new MillerThomsonLLP(), new MorganLewis(), new Ogier(),
            new Ogletree(), new PAGBAM(), new PaulHastings(), new PeterAndKim(), new PeterkaAndPartners(),
            new PillsburyWinthropShawPittman(), new RelianceCorporateAdvisors(), new RopesAndGray(), new Schoenherr(), new Secretariat(),
            new SheppardMullin(), new Skadden(), new SpencerWest(), new SprusonAndFerguson(), new SquirePattonBoggs(),
            new StephensonHarwood(), new StewartMcKelvey(), new SullivanAndWorcester(), new TahotaLaw(), new TaylorWessing(),
            new Walkers(), new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WilliamFry(), new Willkie(),
            new WinstonAndStrawn(), new HadefAndPartners(),
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
     * Builds the list of ByPage firms based on enabled continents.
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
