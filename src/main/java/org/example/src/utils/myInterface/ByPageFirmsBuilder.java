package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
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
            new TheartMey(),
    };

    private static final Site[] ASIA = {
            new ABNR(), new ABSAndCo(), new ADCOLaw(), new Aequitas(), new AllBrightLaw(),
            new AllenAndGledhill(), new ALMTLegal(), new AMTLaw(), new AnandAndAnand(), new AquinasLawAlliance(),
            new AronTadmorLevy(), new ASAndHCliffordChance(), new AssegafHamzahAndPartners(), new AtsumiAndSakai(), new BaeKimAndLee(),
            new BhandariNaqviRiaz(), new BharuchaAndPartners(), new BraunPartners(), new BSALaw(), new BTGAdvaya(),
            new CFNLaw(), new ChandlerMHM(), new DaiichiFuyo(), new Deacons(), new DhavalVussonjiAndAssociates(),
            new DRAndAJU(), new DrewAndNapier(), new EastAndConcord(), new FironLaw(), new FirstLawPC(),
            new FJAndGDeSaram(), new FoxAndMandal(), new GrandwayLaw(), new GulapaLaw(), new GuoyaoQindaoLaw(),
            new Helmsman(), new HFAndCo(), new HiswaraBunjaminAndTandjung(), new Hiways(), new HowseWilliams(),
            new HugillAndIp(), new IndiaLawOffices(), new JeffLeongPoonAndWong(), new JiaYuanLaw(), new JinchengTongdaAndNeal(),
            new JTJBInternationalLawyers(), new JuslawsAndConsult(), new K1Chamber(), new KECOLegal(), new KhaitanAndCo(),
            new KochharAndCo(), new KojimaLaw(), new LeeAndKo(), new Lektou(), new LNT(),
            new MakesAndPartners(), new MASLaw(), new MeitarLaw(), new MohsinTayebalyAndCo(), new MomoOMatsuoAndNamba(),
            new MoriHamadaAndMatsumoto(), new MorogluArseven(), new MZMLegal(), new NagashimaOhnoAndTsunematsu(), new NishimuraAndAsahi(),
            new OhEbashiLPCAndPartners(), new OldhamLiAndNie(), new Paksoy(), new RahmatLimAndPartners(), new RamdasAndWong(),
            new RomuloLawFirm(), new SagaLegal(), new SEUM(), new SFKSLaw(), new ShinAndKim(),
            new SHorowitzAndCo(), new SokSiphanaAndAssociates(), new SteinmetzHaringGurman(), new StevensonWongAndCo(), new TianYuan(),
            new TiruchelvamAssociates(), new TMIAssociates(), new Trilegal(), new TsarAndTsai(), new VellaniAndVellani(),
            new VeritasLegal(), new Virtus(), new VisionAndAssociates(), new WongPartnership(), new Yulchon(),
            new Amicus(), new AnhadLaw(), new DhirAndDhir(), new DLAndFDeSaram(), new LegacyLaw(),
            new Llinks(), new OcampoAndSuralvo(), new PSL(), new PunoLaw(), new SinghaniaAndPartners(),
            new VALaw(), new ZaidIbrahimAndCo(), new ZhongziLaw(),
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
            new DinovaRusevAndPartners(), new DMSLegal(), new Dompatent(), new DrzewieckiTomaszek(), new DZPLaw(),
            new EldibAdvocates(), new Elverdam(), new Esche(), new Eubelius(), new Fidal(),
            new FilipAndCompany(), new Finreg360(), new FIVERS(), new FlichyGrange(), new FluegelPreissner(),
            new Foyen(), new FranklinLaw(), new GanadoAdvocates(), new GorrissenFederspiel(), new GPK(),
            new GrataInternational(), new GreenHorseLegal(), new Haavind(), new HabrakenRutten(), new HammarskioldAndCo(),
            new HannesSnellman(), new HarperJamesSolicitors(), new Hayes(), new HCRLegal(), new Hellstrom(),
            new Hellstrom(), new HjulmandCaptain(), new Holst(), new HPPAttorneys(), new Hugel(),
            new Jalsovszky(), new Jalsovszky(), new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(),
            new Kallan(), new Kanter(), new KewLaw(), new KienhuisLegal(), new Knijff(),
            new Knijff(), new KnezovicAndAssociates(), new KochanskiAndPartners(), new Kolster(), new Krogerus(),
            new KromannReumert(), new LaszczukAndWspolnicy(), new LemstraVanDerKorst(), new LEXIA(), new LEXLogmannsstofa(),
            new Liedekerke(), new Logos(), new LPAGGV(), new Lydian(), new MAQS(),
            new Matheson(), new MazantiAndersen(), new MccannFitzGerald(), new McDermottWillAndEmery(), new McDermottWillAndEmery(),
            new MellingVoitishkinAndPartners(), new MerilampiAttorneys(), new MitelAndAsociatii(), new MoalemWeitemeyer(), new Molinari(),
            new MooreLegalKovacs(), new MSP(), new MVJMarkovicVukoticJovkovic(), new MVVPAdvocaten(), new NielsenNorager(),
            new Njord(), new NOEWE(), new Norens(), new NovaLaw(), new NPPLegal(),
            new NunzianteMagrone(), new NysinghAdvocatenNotarissenNV(), new Odigo(), new Orrick(), new PanettaConsultingGroup(),
            new PayetReyCauviPerez(), new Pedersoli(), new PelsRijcken(), new Penta(), new PFPLaw(),
            new Ploum(), new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(), new PricaAndPartners(), new ProskauerRose(),
            new RadulescuAndMusoi(), new RBK(), new RocaJunyent(), new RoedlAndPartner(), new Roschier(),
            new RPCLegal(), new SchindlerAttorneys(), new SchurtiPartners(), new SelihAndPartnerji(), new Selmer(),
            new Sidley(), new SIRIUS(), new Stibbe(), new StoneKing(), new SuarezDeVivero(),
            new ThomasBodstrom(), new Titov(), new TucaZbarcea(), new VanDerPutt(), new VBAdvocates(),
            new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(), new WolfTheiss(), new ZamfirescuRacotiPredoiu(),
            new AumentoLawFirm(), new EuclidLaw(),
    };

    private static final Site[] NORTH_AMERICA = {
            new BarristonLaw(), new BennettJones(), new BLGLaw(), new BurnetDuckworthAndPalmer(), new Cassels(),
            new ClarkWilson(), new CozenOConnor(), new DaleAndLessmann(), new DeethWilliamsWall(), new DillonEustace(),
            new ECLegalRubio(), new FillmoreRiley(), new FoglerRubinoff(), new Goodmans(), new GrahamThompson(),
            new HeadrickRizikAlvarezAndFernandez(), new HNA(), new Langlois(), new LawsonLundell(), new LEGlobal(),
            new LoopstraNixon(), new McDougallGauley(), new McKercher(), new MijaresAngoitiaCortesAndFuentes(), new MLTAikins(),
            new NautaDutilh(), new NelliganLaw(), new OslerHoskinAndHarcourt(), new OyenWiggs(), new RitchMueller(),
            new RitchMuellerAndNicolau(), new SantamarinaAndSteta(), new SmartAndBiggar(), new StikemanElliott(), new UlisesCabrera(),
            new VillarrealVGF(), new WildeboerDellelce(),
    };

    private static final Site[] CENTRAL_AMERICA = {
            new GarciaBodan(), new McConnellValdes(),
    };

    private static final Site[] SOUTH_AMERICA = {
            new AbeledoGottheil(), new AguayoEcclefieldAndMartinez(), new AllendeAndBrea(), new AlvarezAbogados(), new Andersen(),
            new BaptistaLuz(), new BarriosAndFuentes(), new BarrosAndErrazuriz(), new BeccarVarela(), new Bermudes(),
            new Bocater(), new BrasilSalomaoeMatthes(), new BSVV(), new BullrichFlanzbaum(), new BustamanteFabara(),
            new CARAdvogados(), new CEPDAbogados(), new ChevezRuizZamarripa(), new EsguerraJHR(), new FarrocoAbreuGuarnieriZotelli(),
            new Felsberg(), new FLH(), new GSALegal(), new HernandezAndCia(), new LacazMartinsPereiraNetoGurevichAndSchoueri(),
            new LEFOSSE(), new MachadoMeyer(), new MattosEngelbergEchenique(), new MENPA(), new MontauryPimentaMachadoAndVieiraDeMello(),
            new MQMGLD(), new MUC(), new MunizLaw(), new NelsonWiliansAndAdvogados(), new OlarteMoure(),
            new PayetReyCauviPerez(), new PayetReyCauviPerez(), new PPOAbogados(), new RennoPenteadoSampaioAdvogados(), new RobortellaEPeres(),
            new SerranoMartinezCMA(), new TauilAndChequer(), new Tavares(), new TraviesoEvansArriaAndRengel(), new Tumnet(),
            new Vouga(), new WaldAntunesVitaEBlattner(),
    };

    private static final Site[] OCEANIA = {
            new AiGroup(), new AndersonLloyd(), new AnthonyHarper(), new ArnoldBlochLeibler(), new BankiHaddockFiora(),
            new BuddleFindlay(), new CarterNewell(), new ClaytonUtz(), new Corrs(), new DBHLaw(),
            new DuncanCotterill(), new GordonLegal(), new HarmosHortonLusk(), new HoldingRedlich(), new JamesAndWells(),
            new JohnsonWinterSlattery(), new LaneNeave(), new MacphersonKelley(), new MalleyAndCo(), new MBIP(),
            new MinterEllisonRuddWatts(), new PiperAlderman(), new ThomsonGeer(), new WynnWilliams(),
            new SierraLegal(),
    };

    private static final Site[] MUNDIAL = {
            new ABAndDavid(), new AddleshawGoddardLLP(), new Allens(), new AlTamimi(), new ApplebyGlobal(),
            new ArnoldAndPorter(), new Ashurst(), new AVMAdvogados(), new AWA(), new BCLPLaw(),
            new Blakes(), new BNT(), new CareyOlsen(), new CassidyLevyKent(), new Chattertons(),
            new ClearyGottlieb(), new CliffordChance(), new Conyers(), new CovingtonAndBurlingLLP(), new CrowellAndMoring(),
            new DCCLaw(), new DebevoiseAndPlimpton(), new DuaneMorris(), new FaegreDrinkerBiddleAndReath(), new FRA(),
            new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodwin(), new GreenbergTraurig(), new HadefAndPartners(),
            new HakunLaw(), new HavelPartners(), new HaynesAndBoone(), new HBNLaw(), new HerbertSmithFreehillsKramer(),
            new HFW(), new HillDickinson(), new HollandAndKnight(), new HuntonAndrewsKurth(), new JonesDay(),
            new JSKAdvokatni(), new Kinstellar(), new KISCHIP(), new LathamAndWatkins(), new MagnussonLaw(),
            new MayerBrown(), new McCarthyTetrault(), new Milbank(), new MillerThomsonLLP(), new MorganLewis(),
            new Ogier(), new Ogletree(), new PAGBAM(), new PaulHastings(), new PeterAndKim(),
            new PeterkaAndPartners(), new PillsburyWinthropShawPittman(), new RelianceCorporateAdvisors(), new RopesAndGray(), new Schoenherr(),
            new Secretariat(), new SheppardMullin(), new Skadden(), new SpencerWest(), new SprusonAndFerguson(),
            new SquirePattonBoggs(), new StephensonHarwood(), new StewartMcKelvey(), new SullivanAndWorcester(), new TahotaLaw(),
            new TaylorWessing(), new Walkers(), new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WilliamFry(),
            new Willkie(), new WinstonAndStrawn(),
    };

    // ==================== GETTERS BY CONTINENT ====================

    public static Site[] getAfrica()         { return AFRICA; }
    public static Site[] getAsia()           { return ASIA; }
    public static Site[] getEurope()         { return EUROPE; }
    public static Site[] getNorthAmerica()   { return NORTH_AMERICA; }
    public static Site[] getCentralAmerica() { return CENTRAL_AMERICA; }
    public static Site[] getSouthAmerica()   { return SOUTH_AMERICA; }
    public static Site[] getOceania()        { return OCEANIA; }
    public static Site[] getMundial()        { return MUNDIAL; }

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
