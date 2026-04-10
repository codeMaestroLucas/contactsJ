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
            new Adsero(), new AFMpanga(), new ALPNGAndCo(), new AlukoAndOyebode(), new AmanAndPartners(),
            new Ashitiva(), new BentsiEnchillLetsaAndAnkomah(), new CliffeDekkerHofmeyr(), new CoxYeats(), new EldibAdvocates(),
            new EngoruMutebi(), new FisherQuarmbyAndPfeifer(), new KantorAndImmerman(), new ShahidLaw(), new SPAAjibadeAndCo(),
            new STBB(), new TheartMey(), new TripleOKLaw(),
    };

    private static final Site[] ASIA = {
            new AbeIkuboAndKatayama(), new ABNR(), new ABSAndCo(), new ACIP(), new ADCOLaw(),
            new AdnanSundraAndLowP(), new Aequitas(), new AlHamadLegal(), new AllBrightLaw(), new AllenAndGledhill(),
            new AlMarkazLaw(), new ALMTLegal(), new Amicus(), new AMTLaw(), new AnandAndAnand(),
            new AnhadLaw(), new Anli(), new AquinasLawAlliance(), new AronTadmorLevy(), new ASAndHCliffordChance(),
            new AssegafHamzahAndPartners(), new AtsumiAndSakai(), new BaeKimAndLee(), new BeijingEastIP(), new BhandariNaqviRiaz(),
            new BharuchaAndPartners(), new BraunPartners(), new BSALaw(), new BTGAdvaya(), new CFNLaw(),
            new ChandlerMHM(), new ChuoSogoLPC(), new DaiichiFuyo(), new Deacons(), new DehehengLaw(),
            new DhavalVussonjiAndAssociates(), new DhirAndDhir(), new DLAndFDeSaram(), new DRAndAJU(), new DrewAndNapier(),
            new EastAndConcord(), new FironLaw(), new FirstLawPC(), new FJAndGDeSaram(), new FKNKLaw(),
            new FoxAndMandal(), new GKCPartners(), new GLAAndCompany(), new GrandwayLaw(), new GrossAndCo(),
            new GulapaLaw(), new GuoyaoQindaoLaw(), new Helmsman(), new HFAndCo(), new HiswaraBunjaminAndTandjung(),
            new Hiways(), new HowseWilliams(), new HugillAndIp(), new IndiaLawOffices(), new JeffLeongPoonAndWong(),
            new JiaYuanLaw(), new JinchengTongdaAndNeal(), new JIPYONG(), new JTJBInternationalLawyers(), new JuslawsAndConsult(),
            new K1Chamber(), new KaiRongLaw(), new KECOLegal(), new KhaitanAndCo(), new KimChangAndLee(),
            new KLaw(), new KochharAndCo(), new KojimaLaw(), new LeeAndKo(), new LegacyLaw(),
            new Lektou(), new Llinks(), new LNT(), new MakesAndPartners(), new MASLaw(),
            new MeitarLaw(), new MochtarKaruwinKomar(), new MohsinTayebalyAndCo(), new MomoOMatsuoAndNamba(), new MoriHamadaAndMatsumoto(),
            new MorogluArseven(), new MZMLegal(), new NagashimaOhnoAndTsunematsu(), new NumenLaw(), new OcampoAndSuralvo(),
            new OhEbashiLPCAndPartners(), new OldhamLiAndNie(), new Paksoy(), new PhoenixLegal(), new PSL(),
            new PunoLaw(), new RahmatLimAndPartners(), new RamdasAndWong(), new RomuloLawFirm(), new RP(),
            new SagaLegal(), new SarafAndPartners(), new SEUM(), new SFKSLaw(), new SGLALaw(),
            new ShandongDehengLaw(), new ShinAndKim(), new ShookLinAndBokP(), new ShookLinBok(), new SHorowitzAndCo(),
            new SinghaniaAndPartners(), new SoemadipradjaAndTaher(), new SokSiphanaAndAssociates(), new SteinmetzHaringGurman(), new StevensonWongAndCo(),
            new TianYuan(), new TiruchelvamAssociates(), new TMIAssociates(), new Trilegal(), new TsarAndTsai(),
            new UshijimaAndPartners(), new VALaw(), new VanguardLawyersTokyo(), new VellaniAndVellani(), new VeritasLegal(),
            new Virtus(), new VisionAndAssociates(), new WongPartnership(), new YoonAndYang(), new YuenLaw(),
            new Yulchon(), new ZaidIbrahimAndCo(), new ZhongziLaw(),
    };

    private static final Site[] EUROPE = {
            new Aera(), new AGPAdvokater(), new Alliotts(), new AlstonAndBirdLLP(), new Amorys(),
            new AraozAndRueda(), new ArnesenIP(), new ArnoldAndSiedsma(), new ARQUIS(), new ASCHukuk(),
            new AtlasAdvokater(), new AugustDebouzy(), new AumentoLawFirm(), new BAHR(), new BancilaDiaconuSiAsociatii(),
            new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new BARGERPREKOP(), new Berggren(), new Bernitsas(),
            new BlakeMorgan(), new BlandyAndBlandy(), new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(),
            new Broseta(), new BrownRudnick(), new BSJP(), new BullAndCo(), new BureauPlattner(),
            new BUREN(), new BussMurtonLaw(), new ByrneWallace(), new BYRO(), new CampbellsLegal(),
            new CastrenAndSnellman(), new CBA(), new Cirio(), new Clarkslegal(), new ClemensLaw(),
            new CLPLaw(), new Codex(), new CollasCrill(), new Contrast(), new CravathSwaineAndMoore(),
            new CWAAssociates(), new DahlLaw(), new DavisPolkAndWardwell(), new DechertLLP(), new DeClercq(),
            new Delcade(), new DimitrovPetrovAndCo(), new DinovaRusevAndPartners(), new DKCO(), new DMSLegal(),
            new DrzewieckiTomaszek(), new DZPLaw(), new EldibAdvocates(), new Elverdam(), new Esche(),
            new Esin(), new Eubelius(), new EuclidLaw(), new Fidal(), new FilipAndCompany(),
            new Finreg360(), new FIVERS(), new FlichyGrange(), new FluegelPreissner(), new Foyen(),
            new FranklinLaw(), new GanadoAdvocates(), new GleissLutz(), new GorrissenFederspiel(), new GPK(),
            new GrataInternational(), new GreenHorseLegal(), new GvWGrafvonWestphalen(), new Haavind(), new HabrakenRutten(),
            new HammarskioldAndCo(), new HannesSnellman(), new HarperJamesSolicitors(), new HCRLegal(), new Hellstrom(),
            new HjulmandCaptain(), new Holst(), new HPPAttorneys(), new Hugel(), new Jalsovszky(),
            new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new Kallan(), new Kanter(),
            new KCGPartners(), new KewLaw(), new KienhuisLegal(), new KnezovicAndAssociates(), new Knijff(),
            new KochanskiAndPartners(), new Kolster(), new Krogerus(), new KromannReumert(), new LaszczukAndWspolnicy(),
            new LemstraVanDerKorst(), new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Logos(),
            new LPAGGV(), new Lydian(), new LYND(), new MaleckiLegal(), new MAQS(),
            new Matheson(), new MazantiAndersen(), new MccannFitzGerald(), new McDermottWillAndEmery(), new MellingVoitishkinAndPartners(),
            new MerilampiAttorneys(), new MitelAndAsociatii(), new MMDAdvokati(), new MoalemWeitemeyer(), new Molinari(),
            new MooreLegalKovacs(), new MSP(), new MusatAsociatii(), new MVJMarkovicVukoticJovkovic(), new MVVPAdvocaten(),
            new NaschitzBrandesAmir(), new Niedermüller(), new NielsenNorager(), new Njord(), new NOEWE(),
            new Norens(), new NovaLaw(), new NPPLegal(), new NunzianteMagrone(), new NysinghAdvocatenNotarissenNV(),
            new Odigo(), new Orrick(), new PanettaConsultingGroup(), new PatrikiosPavlouAndAssociates(), new PayetReyCauviPerez(),
            new Pedersoli(), new Penta(), new PFPLaw(), new Ploum(), new PolitisPartners(),
            new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(), new PricaAndPartners(), new ProskauerRose(), new RadovanovicStojanovic(),
            new RadulescuAndMusoi(), new RambergAdvokater(), new RBK(), new RBLaw(), new RocaJunyent(),
            new RoedlAndPartner(), new Roschier(), new RPCLegal(), new SalusLegal(), new SchindlerAttorneys(),
            new Schjodt(), new SchurtiPartners(), new SelihAndPartnerji(), new Selmer(), new Sidley(),
            new SIRIUS(), new SkauReipurth(), new Snellman(), new SokolNovakTrojanDolecek(), new Stibbe(),
            new StoneKing(), new SuarezDeVivero(), new ThomasBodstrom(), new Titov(), new TucaZbarcea(),
            new VanDerPutt(), new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(),
            new WIDEN(), new WolfTheiss(), new ZamfirescuRacotiPredoiu(),
    };

    private static final Site[] AMERICAS = {

// North America
            new BarristonLaw(), new BennettJones(), new BLGLaw(), new BurnetDuckworthAndPalmer(), new Cassels(),
            new Chanis(), new ClarkWilson(), new CozenOConnor(), new DaleAndLessmann(), new DeethWilliamsWall(),
            new DillonEustace(), new ECLegalRubio(), new Farris(), new FillmoreRiley(), new FoglerRubinoff(),
            new Goodmans(), new GrahamThompson(), new HeadrickRizikAlvarezAndFernandez(), new HNA(), new Langlois(),
            new LawsonLundell(), new LEGlobal(), new LoopstraNixon(), new McDougallGauley(), new McKercher(),
            new MijaresAngoitiaCortesAndFuentes(), new MLTAikins(), new NautaDutilh(), new NelliganLaw(), new ONeillAndBorges(),
            new OslerHoskinAndHarcourt(), new OyenWiggs(), new Patterson(), new RamosRipollSchuster(), new RitchMueller(),
            new RitchMuellerAndNicolau(), new SantamarinaAndSteta(), new SmartAndBiggar(), new StikemanElliott(), new Uhthoff(),
            new UlisesCabrera(), new VillarrealVGF(), new WildeboerDellelce(),

// Central America
            new DelcoLaw(), new GalindoAriasLopez(), new GarciaBodan(), new LexAtlas(), new Lexincorp(),
            new McConnellValdes(), new Nassar(),

// South America
            new AbeledoGottheil(), new AguayoEcclefieldAndMartinez(), new AllendeAndBrea(), new AlvarezAbogados(), new Andersen(),
            new BaptistaLuz(), new BarriosAndFuentes(), new BarrosAndErrazuriz(), new BeccarVarela(), new Bermudes(),
            new Bocater(), new BrasilSalomaoeMatthes(), new BSVV(), new BullrichFlanzbaum(), new BustamanteFabara(),
            new CARAdvogados(), new CEPDAbogados(), new ChevezRuizZamarripa(), new EsguerraJHR(), new FarrocoAbreuGuarnieriZotelli(),
            new Felsberg(), new FLH(), new GNBLaw(), new GSALegal(), new HernandezAndCia(),
            new LacazMartinsPereiraNetoGurevichAndSchoueri(), new LEFOSSE(), new LRILaw(), new MachadoMeyer(), new MattosEngelbergEchenique(),
            new MENPA(), new MontauryPimentaMachadoAndVieiraDeMello(), new MQMGLD(), new MUC(), new MunizLaw(),
            new NelsonWiliansAndAdvogados(), new OlarteMoure(), new PalaciosLleras(), new PalmaLaw(), new PayetReyCauviPerez(),
            new PPOAbogados(), new RennoPenteadoSampaioAdvogados(), new RobortellaEPeres(), new SerranoMartinezCMA(), new TauilAndChequer(),
            new Tavares(), new TraviesoEvansArriaAndRengel(), new Tumnet(), new Vouga(), new WaldAntunesVitaEBlattner(),
    };

    private static final Site[] OCEANIA = {
            new AiGroup(), new AndersonLloyd(), new AnthonyHarper(), new ArnoldBlochLeibler(), new BankiHaddockFiora(),
            new BuddleFindlay(), new CarterNewell(), new ClaytonUtz(), new Corrs(), new DBHLaw(),
            new DoogueGeorge(), new DuncanCotterill(), new FpaPatent(), new GordonLegal(), new HarmosHortonLusk(),
            new HoldingRedlich(), new JamesAndWells(), new JohnsonWinterSlattery(), new LaneNeave(), new MacphersonKelley(),
            new MalleyAndCo(), new MayneWetherell(), new MBIP(), new MinterEllisonRuddWatts(), new MorayAndAgnew(),
            new PiperAlderman(), new SierraLegal(), new ThomsonGeer(), new WynnWilliams(),
    };

    private static final Site[] MUNDIAL = {
            new ABAndDavid(), new AddleshawGoddardLLP(), new Allens(), new AlTamimi(), new ApplebyGlobal(),
            new ArnoldAndPorter(), new Ashurst(), new AVMAdvogados(), new AWA(), new BCLPLaw(),
            new Blakes(), new BNT(), new CareyOlsen(), new CassidyLevyKent(), new Chattertons(),
            new ClearyGottlieb(), new CliffordChance(), new Conyers(), new CorporateINTL(), new CovingtonAndBurlingLLP(),
            new CrowellAndMoring(), new DCCLaw(), new DebevoiseAndPlimpton(), new DuaneMorris(), new FaegreDrinkerBiddleAndReath(),
            new FRA(), new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodwin(), new GreenbergTraurig(),
            new HadefAndPartners(), new HakunLaw(), new HavelPartners(), new HaynesAndBoone(), new HBNLaw(),
            new HFW(), new HillDickinson(), new HollandAndKnight(), new HuntonAndrewsKurth(), new JonesDay(),
            new JSKAdvokatni(), new Kinstellar(), new KISCHIP(), new KRySGlobal(), new LathamAndWatkins(),
            new LPALaw(), new MagnussonLaw(), new MayerBrown(), new McCarthyTetrault(), new Milbank(),
            new MillerThomsonLLP(), new MorganLewis(), new Ogier(), new Ogletree(), new PAGBAM(),
            new PaulHastings(), new PeterAndKim(), new PeterkaAndPartners(), new PillsburyWinthropShawPittman(), new RelianceCorporateAdvisors(),
            new Rimon(), new RopesAndGray(), new Schoenherr(), new Secretariat(), new SheppardMullin(),
            new Skadden(), new SpencerWest(), new SprusonAndFerguson(), new SquirePattonBoggs(), new StewartMcKelvey(),
            new SullivanAndWorcester(), new TahotaLaw(), new TaylorWessing(), new Walkers(), new WatsonFarleyAndWilliams(),
            new WhiteAndCase(), new WilliamFry(), new Willkie(), new WinstonAndStrawn(),
    };

    private static final Site[] TEST = {};

    // ==================== GETTERS BY CONTINENT ====================

    public static Site[] getAfrica()   { return AFRICA; }
    public static Site[] getAsia()     { return ASIA; }
    public static Site[] getEurope()   { return EUROPE; }
    public static Site[] getAmericas() { return AMERICAS; }
    public static Site[] getOceania()  { return OCEANIA; }
    public static Site[] getMundial()  { return MUNDIAL; }

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
        if (ContinentConfig.isContinentEnabled("Americas")) sites.addAll(Arrays.asList(AMERICAS));
        if (ContinentConfig.isContinentEnabled("Oceania")) sites.addAll(Arrays.asList(OCEANIA));

        // Mundial is always included (global firms)
        sites.addAll(Arrays.asList(MUNDIAL));

        return sites.toArray(new Site[0]);
    }
}
