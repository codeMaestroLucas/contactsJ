package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites_to_test.*;
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
            new ZakiHashemAndPartners(), new ZulficarAndPartners(),
            new Ortus(), new PerchstoneAndGraeys(), new RDSPartners(), new RONNLaw(), new Shalakany(),
            new AbrahamsAndGrossIncorporated(), new AllianceLaw(), new BarnardInc(), new BirungyiBarataAndAssociates(),
            new BLCRobertAndAssociatesLtd(), new Bookbinder(), new CorpusLegal(), new EnglingStritterAndPartners(), new ENRAdvisory(),
            new GillGodlontonAndGerrans(), new HJW(), new IbrachyAndDermarkar(), new IkeyiShittuAndCo(), new KOAssociates(),
            new MMAKS(), new MMAN(), new MulengaMundashiLegalPractitioners(), new MusibauAdetunbiAndCo(), new MuvingiAndMugadza(),
            new OraroAndCompany(), new RobinsonBertram(), new SenekalSimmonds(), new SLKB(),
            new AdamsAndAdams(), new AfricaseAttorneys(), new ALCAlieldeanWeshahi(), new AshongBenjamin(), new BennaniAndAssocies(),
            new TNP(), new JLDAndMBLegal(), new ChazaiWamba(), new DualeOviaAlexAdedipe(),
    };

    private static final Site[] ASIA = {
            new AdnanSundraAndLowNP(), new AgmonWithTulchinsky(), new AJUKimChangAndLee(), new AOil(), new ArethaLegal(),
            new AscendantLegal(), new AsiaLiuhIP(), new AtlasLaw(), new AxisLawChambers(), new AzmanDavidsonAndCo(),
            new BalterGuthAloniAndCo(), new BarneaAndCo(), new BoaseCohenAndCollins(), new BrossAndPartners(), new CapitalEquityLegalGroup(),
            new ChadhaAndCo(), new ChienYehLaw(), new CityYuwaPartners(), new CorneliusLaneAndMufti(), new CovenantChambers(),
            new CTPartners(), new Dashnyam(), new DesaiAndDiwanji(), new Docvit(),
            new DomnernSomgiatAndBoonma(), new DonaldsonAndBurkinshaw(), new DSKLegal(), new EBN(), new EconomicLawsPractice(),
            new EJELaw(), new EldanLaw(), new EligGurkaynak(), new ErdemAndErdem(), new ErgunAvukatlikBurosu(),
            new FCLaw(), new Fischer(), new FoongAndPartners(), new Gall(), new GanLeeAndTan(),
            new GlobalLawOffice(), new GlobalVietnamLawyers(), new GornitzkyAndCo(), new HaidermotaAndCo(), new HalimHongAndQuek(),
            new HarryElias(), new HastingsAndCo(), new Hauzen(), new HHRLawyers(),
            new HornAndCo(), new HuiyeLaw(), new HYLeungAndCo(), new KRBLaw(), new LeeAndLee(),
            new LepaMeirAndCo(), new LonganLaw(), new LSHorizon(), new MatryMeiriAndCo(), new MDLaw(),
            new MishconKaras(), new MiuraAndPartners(), new MohanadassPartnership(), new Mourant(), new NurmansyahAndMuzdalifah(),
            new ONC(), new OneAsiaLawyers(), new QuaheWooAndPalmer(), new QuiasonMakalintal(), new RCLChambersLaw(),
            new RemfryAndSagar(), new RIAABarkerGillette(), new RobertsonsSolicitors(), new SamvadPartners(), new SayatZholshyAndPartners(),
            new SCPT(), new SGAndCoLawyers(), new ShanghaiPacificLegal(), new ShookLinAndBokNP(), new ShoobAndCo(),
            new SIPLaw(), new SolomonAndCo(), new SNRAssociates(), new Stellex(), new StratageLaw(),
            new SudathPerera(), new TannerDeWitt(), new TCLaw(), new TheCapitalLaw(),
            new TMPIntellectualProperty(), new TokyoInternationalLaw(), new TommyThomas(), new TTA(), new TTTAndPartners(),
            new VILAF(), new WooKwanLeeAndLo(), new YossiLevyAndCo(), new ZhongLunLaw(),
            new AnJieBroad(), new BaohuaLaw(), new ChangTsiAndPartners(), new DeHeng(), new DowwayAndPartners(),
            new LanbaiLaw(), new RiverDeltaLaw(), new TALaw(), new WangJingAndCo(),
            new YYCLegal(), new SASLO(), new ShardulAmarchandMangaldasAndCo(), new ShiboletAndCo(), new SIGNUM(),
            new NunoSimoesAndAssociados(), new PKWongAndNair(), new PremierChambers(), new ProvidenceLaw(), new RajaniAssociates(),
            new MachasAndPartners(), new MaheshwariAndCo(), new MAQLegal(), new MDPAndPartners(), new RSM(),
            new JunZeJunLaw(), new KingStubbAndKasiva(), new LAWPartnership(), new LHAG(),
            new Meysan(), new TANLaw(), new ERM(),
            new AECO(), new AlBusaidyMansoorJamal(), new AlYaqoutAlFouzan(), new ANSS(),
            new ASAR(), new BLCASSOCIATES(), new BossYoung(), new BunAssociates(),
            new AlAlawiCo(), new ZanHub(),
            new CC(), new ChanceBridge(), new CNPLaw(), new CoEffortLaw(), new CollyerLaw(),
            new DavidsonAndCo(), new DFDL(), new Frasers(),
            new KanKrishme(), new LeeInternational(), new LeeTsaiAndPartners(), new ManuelaAntonio(), new OonBazul(),
    };

    private static final Site[] EUROPE = {
            new ABGIP(), new ACAndR(), new Astrea(), new Avance(), new BadriAndSalimElMeouchiLaw(),
            new Beauchamps(), new Belgravia(), new BettenAndResch(), new BonnAndSchmitt(), new Borenius(),
            new Boyanov(), new BRAUNEISRECHTSANWALTE(), new BurgesSalmon(), new CarneluttiLaw(), new Cerraloglu(),
            new CRCCD(), new DANUBIAPatentAndLaw(), new DGKV(), new Dottir(), new EisenfuhrSpeiserAndPartner(),
            new Ekelmans(), new EllisonsSolicitors(), new EPAndC(), new FCMLimited(), new FPSLaw(),
            new FrancisWilksAndJones(), new Frontier(), new Fylgia(), new GittiAndPartners(), new GORG(),
            new GreeneAndGreeneSolicitors(), new GunAndPartners(), new GVZH(), new Hamso(), new HarteBavendamm(),
            new HaslingerNagele(), new Hayes(), new HoffmannEitle(), new Holmes(), new Horten(),
            new HoxhaMemiAndHoxha(), new JadekAndPensa(), new JBLaw(), new JWP(), new KallioLaw(),
            new KambourovAndPartners(), new KBVLLaw(), new KennedyVanderLaan(), new KeystoneLaw(), new KLCLaw(),
            new KolcuogluDemirkanKocakli(), new Kondrat(), new KonecnaAndZacha(), new Kvale(), new KWKRLaw(),
            new LambadariosLaw(), new LangsethAdvokat(), new Legalis(), new LePooleBekema(), new Lindahl(),
            new MaikowskiAndNinnemann(), new MeyerKoring(), new MorrisLaw(), new MSBSolicitors(), new NESTOR(),
            new OgletreeDeakins(), new OneEssexCourt(), new Onsagers(), new ONVLaw(), new Oppenheim(),
            new Oxera(), new PhilippeAndPartners(), new PinneyTalfourdSolicitors(), new PMP(), new PortaAndConsulentiAssociati(),
            new Poulschmith(), new PrinzAndPartner(), new PrueferAndPartner(), new PuschWahlig(), new Racine(),
            new isarpatentMunchen(), new RymarzZdortMaruta(), new SampsonCowardLLP(), new SayinLaw(), new SBGK(),
            new Schoups(), new Sherrards(), new SKWSchwarz(), new SlaughterAndMay(), new SRSLegal(),
            new Strelia(), new SZA(), new Szecskay(), new Valfor(), new VanOlmenAndWynant(),
            new Vischer(), new VossiusAndPartner(), new ZampaPartners(), new ZeposAndYannopoulos(), new WardynskiAndPartners(),
            new WengerVieliAG(), new Wiersholm(), new WikborgRein(),
            new SchalastAndPartner(), new SchellenbergWittmer(), new ScottoPartners(), new SimontBraun(),
            new Legance(), new Oppenhoff(), new Orsingher(), new PapapolitisAndPapapolitis(), new PeliPartners(),
            new Pestalozzi(), new PiniFrancoLLP(), new PLMJ(), new POELLATH(),
            new PotamitisVekris(), new PragerDreifuss(), new WierzbowskiAndPartners(),
            new MPRPartners(), new NGLLegal(), new NiederhuberAndPartner(),
            new MENACityLawyers(), new MermozAvocats(), new MFWFialek(), new MihajIlicAndMilanovic(), new MishcondeReya(),
            new DALDEWOLF(), new GLXLTM(), new LenzAndStaehelin(), new LLBerg(), new LMS(),
            new MacedoVitorino(), new Maples(), new ProvencaDeCarvalho(),
            new ServuloAndAssociados(), new LeitnerLawRechtsanwalte(),
            new EvershedsSutherland(), new FortLegal(), new FTPA(), new GeorgievTodorovAndCo(), new GilibertiTriscorniaEAssociati(),
            new GladeMichelWirtz(), new Granrut(), new GrimaldiAlliance(), new GuttOlkFeldhaus(), new IdEstAvocats(),
            new IlejAndPartners(), new Interlaw(), new Kaimakliotis(), new LacourteRaquinTatar(),
            new ErsoyBilgehan(), new HWWHermannWienbergWilhelm(),
            new ADVANTAltana(), new AgioLegal(), new AMERELLER(), new ArendtMedernach(),
            new AVSLegal(), new B2RLaw(), new BDKAdvokati(), new BDGSAssociates(), new BechBruun(),
            new Bener(), new BrandlTalos(), new BulboacaAsociatii(),
    };

    private static final Site[] NORTH_AMERICA = {
            new ArthurCox(), new AsafoAndCo(), new BCFLaw(), new BWBLLP(), new FilionWakelyThorupAngeletti(),
            new IbanezParkman(), new KuriBrena(), new MBM(), new McKinneyBancroftAndHughes(), new NaderHayauxAndGoebel(),
            new PrasadAndCompany(), new Sangra(), new ThompsonDorfmanSweatman(), new VazquezTerceroAndZepeda(),
            new SangraMollerLLP(), new NunesScholefieldDeLeonAndCo(), new PattersonMairHamilton(), new PietrantoniMendezAndAlvarezLLC(),
            new LivingstonAlexanderAndLevy(), new Leaf(), new Pitblado(),
            new BensonBuffett(), new BoyneClarke(),
    };

    private static final Site[] CENTRAL_AMERICA = {
            new MyersFletcherAndGordon(), new MorganAndMorgan(), new Lovill(), new GuzmanAriza(), new IcazaGonzalezRuizAndAleman(),
            new Alcogal(),
    };

    private static final Site[] SOUTH_AMERICA = {
            new BrigrardUrrutia(), new Carey(), new CariolaDiezPerezCotapos(), new Ferrere(), new GaiaSilvaGaedeAndAssociados(),
            new LatinAlliance(), new Madrona(), new NFA(), new RMADVAdvogados(), new SargentAndKrahn(),
            new ZBV(), new PGLaw(), new MHRLegal(), new JDSellierAndCo(),
            new OFarrell(), new AlessandriLawyers(), new Bomchil(),
    };

    private static final Site[] OCEANIA = {
            new AJLawAndCo(), new AitkenPartners(), new Baumgartners(), new Chamberlains(), new ConnollySuthers(),
            new Corcoran(), new DeutschMiller(), new DWFoxTucker(), new Finlaysons(), new Gadens(),
            new GilbertAndTobin(), new Grette(), new HamiltonLocke(), new HeskethHenry(), new Hicksons(),
            new HWEbsworth(), new Madderns(), new MatthewsFolbigg(), new McCulloughRobertson(), new MellorOlsson(),
            new NormanWaterhouse(), new RussellMcVeagh(), new SimmonsWolfhagen(), new TompkinsWake(), new WengerVieliAG(),
            new WilsonHarle(), new WilsonRyanGrose(), new Wrays(), new YoungList(),
            new SimpsonGrierson(), new GreenwoodRoche(),
    };

    private static final Site[] MUNDIAL = {
            new Adna(), new ALGoodbody(), new BDO(), new CerhaHempel(), new Cobalt(),
            new ControlRisks(), new Cuatrecasas(), new Curtis(), new Dentons(), new Ellex(),
            new EProint(), new FangdaPartners(), new GuantaoLaw(), new HiggsAndJohnson(), new Houthoof(),
            new JohnsonCamachoAndSingh(), new JPMAndPartners(), new KingAndWoodMallesons(), new LatamLex(), new Legance(),
            new LewissSilkin(), new LexCaribbean(), new MarksAndClerk(), new MdME(), new METIDA(),
            new MIOLaw(), new Noerr(), new OsborneClarke(), new PearlCohen(), new PortolanoCavallo(),
            new Pulegal(), new QuinEmanuel(), new ReinholdCohnGroup(), new SabaAndCo(), new SimmonsAndSimmons(),
            new Sorainen(), new StephensonHarwood(), new TEMPLARS(), new Thommessen(), new Vaneps(),
            new WALLESS(), new SullivanAndCromwell(), new SdzlegalSchindhelm(),
            new EmploymentLawAlliance(), new RiadSalehAndPartners(), new Luther(),
            new ArochiLindner(), new Bracewell(), new Cooley(),
    };

    private static final Site[] TEST = {
// ByPage - Africa
            new PoswaIncorporated(), // new site coming soon

// ByNewPage - Asia
            new RHTLaw(), new SarmientoLoriegaLaw(), new Skrine(), new SOIP(), new TillekeGibbins(),
            new Tongshang(), new TSMP(), new WithersKhattarWong(), new YKVN(), new YouMe(),

// ByNewPage - Central America
            new DeCampsVasquezVarela(),

// ByNewPage - Europe
            new ACTLEGAL(), new AtlasAdvokater(), new BGLegal(), new CasesLacambra(), new CCALegal(),
            new CCSLegal(), new ChryssesDemetriades(), new Darrois(), new DeGaulleFleurance(), new DePardieu(),
            new Desfilis(), new DSMAvocats(), new EliasNeocleous(), new Finnius(), new GrigorescuStefanica(),
            new Kneppelhout(), new LAGRO(), new Lexence(), new NitschneiderAndPartners(), new RutgersPosch(),
            new VisionConsulting(), new WindtLeGrand(),

// ByNewPage - North America
            new Chaitons(), new CoxAndPalmer(), new CurtisDawe(), new DunnCox(), new Harris(),

// ByNewPage - Oceania
            new ChanceryGreen(),

// ByNewPage - South America
            new DanielLaw(), new DSNavarroCastex(), new MadrugaBTW(), new RojasLawFirm(), new SuarezConsultoria(),
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
