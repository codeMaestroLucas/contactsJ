package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.to_test._standingBy.FGGHIP;
import org.example.src.sites.to_test._standingBy.PerezLlorca;
import org.example.src.sites.to_test._standingBy.PoswaIncorporated;
import org.example.src.sites.to_test.americas.*;
import org.example.src.sites.to_test.europe.*;
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
            new AbrahamsAndGrossIncorporated(), new AdamsAndAdams(), new AfricaseAttorneys(), new ALCAlieldeanWeshahi(), new AllianceLaw(),
            new AshongBenjamin(), new BarnardInc(), new BennaniAndAssocies(), new BirungyiBarataAndAssociates(), new BLCRobertAndAssociatesLtd(),
            new Bookbinder(), new ChazaiWamba(), new CorpusLegal(), new DualeOviaAlexAdedipe(), new EnglingStritterAndPartners(),
            new ENRAdvisory(), new ENSAfrica(), new FatimaFreitas(), new GillGodlontonAndGerrans(), new HansOffiaAndAssociates(),
            new HJW(), new IbrachyAndDermarkar(), new IkeyiShittuAndCo(), new JacksonEttiAndEdu(), new JLDAndMBLegal(),
            new KOAssociates(), new MMAKS(), new MMAN(), new MulengaMundashiLegalPractitioners(), new MusibauAdetunbiAndCo(),
            new MuvingiAndMugadza(), new OraroAndCompany(), new Ortus(), new PerchstoneAndGraeys(), new RDSPartners(),
            new RobinsonBertram(), new RONNLaw(), new SenekalSimmonds(), new Shalakany(), new Shalakany(),
            new SLKB(), new TNP(), new UdoUdomaBeloOsagie(), new Werksmans(), new ZakiHashemAndPartners(),
            new ZulficarAndPartners(),
    };

    private static final Site[] ASIA = {
            new ACCRALAW(), new AdnanSundraAndLowNP(), new AECO(), new AgmonWithTulchinsky(), new AJUKimChangAndLee(),
            new AlAlawiCo(), new AlBusaidyMansoorJamal(), new AlYaqoutAlFouzan(), new AnJieBroad(), new ANSS(),
            new AOil(), new ArcheusLaw(), new ArethaLegal(), new Argus(), new ASAR(),
            new AscendantLegal(), new AsiaLiuhIP(), new AtlasLaw(), new AxisLawChambers(), new AYMP(),
            new AZB(), new AzmanDavidsonAndCo(), new BalterGuthAloniAndCo(), new BaohuaLaw(), new BarneaAndCo(),
            new BLCASSOCIATES(), new BoaseCohenAndCollins(), new BossYoung(), new Bowers(), new BrossAndPartners(),
            new BunAssociates(), new CapitalEquityLegalGroup(), new CC(), new CG(), new ChadhaAndCo(),
            new ChanceBridge(), new ChanceBridge(), new ChangTsiAndPartners(), new ChienYehLaw(), new CityYuwaPartners(),
            new ClasisLaw(), new CNPLaw(), new CoEffortLaw(), new CollyerLaw(), new CorneliusLaneAndMufti(),
            new CovenantChambers(), new CTPartners(), new CyrilAmarchandMangaldas(), new Dashnyam(), new DavidsonAndCo(),
            new DeHeng(), new DesaiAndDiwanji(), new DFDL(), new Docvit(), new DomnernSomgiatAndBoonma(),
            new DonaldsonAndBurkinshaw(), new DowwayAndPartners(), new DSKLegal(), new DuanDuan(), new DuanDuan(),
            new EBN(), new EconomicLawsPractice(), new EJELaw(), new EldanLaw(), new EligGurkaynak(),
            new ErdemAndErdem(), new ErgunAvukatlikBurosu(), new ERM(), new FCLaw(), new FGEEbrahimHosain(),
            new Fischer(), new FoongAndPartners(), new Frasers(), new Gall(), new Gallant(),
            new GanLeeAndTan(), new GlobalLawOffice(), new GlobalVietnamLawyers(), new GornitzkyAndCo(), new GRUBALAW(),
            new HaidermotaAndCo(), new HaiRun(), new HaiRun(), new Haiwen(), new Haiwen(),
            new HalimHongAndQuek(), new HarryElias(), new HastingsAndCo(), new Hauzen(), new HHRLawyers(),
            new HornAndCo(), new HuiyeLaw(), new HuiZhong(), new HuiZhong(), new Hylands(),
            new Hylands(), new HYLeungAndCo(), new JingtianGongcheng(), new JingtianGongcheng(), new JointWin(),
            new JPMLaw(), new JunZeJunLaw(), new KanKrishme(), new KingStubbAndKasiva(), new KRBLaw(),
            new LanbaiLaw(), new LAWPartnership(), new LeeAndLee(), new LeeInternational(), new LeeTsaiAndPartners(),
            new LepaMeirAndCo(), new LHAG(), new LonganLaw(), new LSHorizon(), new LuthraAndLuthra(),
            new MachasAndPartners(), new MaheshwariAndCo(), new ManuelaAntonio(), new MAQLegal(), new MatryMeiriAndCo(),
            new MDLaw(), new MDPAndPartners(), new Meysan(), new MishconKaras(), new MiuraAndPartners(),
            new MohanadassPartnership(), new MosveldttLaw(), new Mourant(), new MVGS(), new NunoSimoesAndAssociados(),
            new NurmansyahAndMuzdalifah(), new ONC(), new OneAsiaLawyers(), new OonBazul(), new PKWongAndNair(),
            new PlatonMartinez(), new Poovayya(), new PremierChambers(), new ProvidenceLaw(), new QuaheWooAndPalmer(),
            new QuiasonMakalintal(), new RajaniAssociates(), new RCLChambersLaw(), new RemfryAndSagar(), new RHTLaw(),
            new RIAABarkerGillette(), new RiverDeltaLaw(), new RobertsonsSolicitors(), new RSM(), new SamvadPartners(),
            new SASLO(), new SayatZholshyAndPartners(), new SCPT(), new SGAndCoLawyers(), new ShanghaiPacificLegal(),
            new ShardulAmarchandMangaldasAndCo(), new ShiboletAndCo(), new ShoobAndCo(), new ShookLinAndBokNP(), new SIGNUM(),
            new SIPLaw(), new Skrine(), new SNRAssociates(), new SoemadipradjaTaher(), new SOIP(),
            new SolomonAndCo(), new Stellex(), new StratageLaw(), new SudathPerera(), new TALaw(),
            new TANLaw(), new TannerDeWitt(), new TCLaw(), new TheCapitalLaw(), new TillekeGibbins(),
            new TMPIntellectualProperty(), new TokyoInternationalLaw(), new TommyThomas(), new Tongshang(), new TSMP(),
            new TTA(), new TTTAndPartners(), new UMBRA(), new Vaish(), new VILAF(),
            new WangJingAndCo(), new WilliamHendrikSiregarDjojonegoro(), new WooKwanLeeAndLo(), new YKVN(), new YossiLevyAndCo(),
            new YouMe(), new YYCLegal(), new ZanHub(), new ZhongLun(), new ZhongLunLaw(),
    };

    private static final Site[] EUROPE = {
            new ABGIP(), new ACAndR(), new ACTLEGAL(), new ADVANTAltana(), new ADVANTBeiten(),
            new AgioLegal(), new AMERELLER(), new ArendtMedernach(), new ASPapadimitriouPartners(), new Astrea(),
            new Avance(), new AVSLegal(), new B2RLaw(), new BadriAndSalimElMeouchiLaw(), new BDGSAssociates(),
            new BDKAdvokati(), new Beauchamps(), new BechBruun(), new Belgravia(), new Bener(),
            new BettenAndResch(), new BGLegal(), new BonnAndSchmitt(), new Borenius(), new Boyanov(),
            new BrandlTalos(), new BRAUNEISRECHTSANWALTE(), new BulboacaAsociatii(), new BurgesSalmon(), new CAINS(),
            new CamilleriPreziosi(), new CarneluttiLaw(), new CasesLacambra(), new CCALegal(), new CCSLegal(),
            new Cerraloglu(), new ChryssesDemetriades(), new CRCCD(), new DALDEWOLF(), new DANUBIAPatentAndLaw(),
            new Darrois(), new DeGaulleFleurance(), new DePardieu(), new Desfilis(), new DGKV(),
            new Dottir(), new DSMAvocats(), new EisenfuhrSpeiserAndPartner(), new Ekelmans(), new EliasNeocleous(),
            new EllisonsSolicitors(), new EPAndC(), new ErsoyBilgehan(), new EvershedsSutherland(), new FCMLimited(),
            new Finnius(), new FortLegal(), new FPSLaw(), new FrancisWilksAndJones(), new Frontier(),
            new FTPA(), new Fylgia(), new GeorgievTodorovAndCo(), new GilibertiTriscorniaEAssociati(), new GittiAndPartners(),
            new GladeMichelWirtz(), new GLXLTM(), new GORG(), new Granrut(), new GreeneAndGreeneSolicitors(),
            new GrigorescuStefanica(), new GrimaldiAlliance(), new GunAndPartners(), new GuttOlkFeldhaus(), new GVZH(),
            new Hamso(), new HarteBavendamm(), new HaslingerNagele(), new Hayes(), new HoffmannEitle(),
            new Holmes(), new Horten(), new HoxhaMemiAndHoxha(), new HWWHermannWienbergWilhelm(), new IdEstAvocats(),
            new IlejAndPartners(), new Interlaw(), new isarpatentMunchen(), new JadekAndPensa(), new JBLaw(),
            new JWP(), new Kaimakliotis(), new KallioLaw(), new KambourovAndPartners(), new KBVLLaw(),
            new KennedyVanderLaan(), new KeystoneLaw(), new KLCLaw(), new Kneppelhout(), new KolcuogluDemirkanKocakli(),
            new Kondrat(), new KonecnaAndZacha(), new Kvale(), new KWKRLaw(), new LacourteRaquinTatar(),
            new LAGRO(), new LambadariosLaw(), new LangsethAdvokat(), new Legalis(), new Legance(),
            new LeitnerLawRechtsanwalte(), new LenzAndStaehelin(), new LePooleBekema(), new Lexence(), new Lindahl(),
            new LLBerg(), new LMS(), new MacedoVitorino(), new MaikowskiAndNinnemann(), new MalagaSolicitors(),
            new Maples(), new MENACityLawyers(), new MermozAvocats(), new MeyerKoring(), new MFWFialek(),
            new MihajIlicAndMilanovic(), new Miranda(), new MishcondeReya(), new MLLLegal(), new MorrisLaw(),
            new MPRPartners(), new MSBSolicitors(), new NagyTrocsanyi(), new NESTOR(), new NGA(),
            new NGLLegal(), new NicolasKanellopoulos(), new NiedererKraftFrey(), new NiederhuberAndPartner(), new OgletreeDeakins(),
            new OneEssexCourt(), new Onsagers(), new ONVLaw(), new Oppenheim(), new Oppenhoff(),
            new Orsingher(), new Oxera(), new PapapolitisAndPapapolitis(), new PeliPartners(), new Pestalozzi(),
            new PHH(), new PhilipLee(), new PhilippeAndPartners(), new PiniFrancoLLP(), new PinneyTalfourdSolicitors(),
            new Plesner(), new PLMJ(), new PMP(), new POELLATH(), new PortaAndConsulentiAssociati(),
            new PotamitisVekris(), new Poulschmith(), new PragerDreifuss(), new PrinzAndPartner(), new ProvencaDeCarvalho(),
            new PrueferAndPartner(), new PuschWahlig(), new Quinz(), new Racine(), new Rask(),
            new ReinholdCohnGroup(), new RKKW(), new RojsPeljhan(), new RTPR(), new RutgersPosch(),
            new RuzickaPartners(), new RymarzZdortMaruta(), new SadkowskiWspolnicy(), new SampsonCowardLLP(), new SANDS(),
            new SayinLaw(), new SBGK(), new SchalastAndPartner(), new SchellenbergWittmer(), new SchneeweissWeixelbaum(),
            new Schoups(), new SchrammOehler(), new ScottoPartners(), new SenicaPartners(), new ServuloAndAssociados(),
            new Setterwalls(), new SGP(), new Sherrards(), new SimonAssocies(), new SimontBraun(),
            new SkilsLaw(), new SkrastinsDzenis(), new SKWSchwarz(), new SlaughterAndMay(), new SouriadakisTsibris(),
            new SRSLegal(), new StehlinAssocies(), new Strelia(), new SZA(), new Szecskay(),
            new Valfor(), new VanOlmenAndWynant(), new Vischer(), new VisionConsulting(), new VossiusAndPartner(),
            new WardynskiAndPartners(), new WengerVieliAG(), new Wiersholm(), new WierzbowskiAndPartners(), new WikborgRein(),
            new WindtLeGrand(), new ZampaPartners(), new ZeposAndYannopoulos(),
    };

    private static final Site[] AMERICAS = {
// North America
            new ArthurCox(), new AsafoAndCo(), new BCFLaw(), new BensonBuffett(), new BoyneClarke(),
            new BWBLLP(), new FilionWakelyThorupAngeletti(), new IbanezParkman(), new KuriBrena(), new Leaf(),
            new LivingstonAlexanderAndLevy(), new MBM(), new McKinneyBancroftAndHughes(), new MedinaGarnesAbogados(), new NaderHayauxAndGoebel(),
            new NunesScholefieldDeLeonAndCo(), new PattersonMairHamilton(), new PietrantoniMendezAndAlvarezLLC(), new Pitblado(), new PrasadAndCompany(),
            new Sangra(), new SangraMollerLLP(), new ThompsonDorfmanSweatman(), new VazquezTerceroAndZepeda(),

// Central America
            new AlburquerqueAbogados(), new Alcogal(), new DeCampsVasquezVarela(), new FabregaMolinoMulino(), new GuzmanAriza(),
            new IcazaGonzalezRuizAndAleman(), new Lovill(), new MorganAndMorgan(), new MyersFletcherAndGordon(),

// South America
            new AlessandriLawyers(), new Bomchil(), new BrigrardUrrutia(), new Carey(), new CariolaDiezPerezCotapos(),
            new Ferrere(), new GaiaSilvaGaedeAndAssociados(), new JDSellierAndCo(), new LatinAlliance(), new Madrona(),
            new MHRLegal(), new NFA(), new OFarrell(), new PGLaw(), new RMADVAdvogados(),
            new SargentAndKrahn(), new ZBV(),
    };

    private static final Site[] OCEANIA = {
            new AitkenPartners(), new AJLawAndCo(), new Baumgartners(), new Chamberlains(), new ConnollySuthers(),
            new Corcoran(), new DeutschMiller(), new DWFoxTucker(), new Finlaysons(), new Gadens(),
            new GilbertAndTobin(), new GreenwoodRoche(), new Grette(), new HamiltonLocke(), new HeskethHenry(),
            new Hicksons(), new HWEbsworth(), new Madderns(), new MatthewsFolbigg(), new McCulloughRobertson(),
            new MellorOlsson(), new NormanWaterhouse(), new RussellMcVeagh(), new SimmonsWolfhagen(), new SimpsonGrierson(),
            new TompkinsWake(), new WengerVieliAG(), new WilsonHarle(), new WilsonRyanGrose(), new Wrays(),
            new YoungList(),
    };

    private static final Site[] MUNDIAL = {
            new Adna(), new ALGoodbody(), new ArochiLindner(), new BDO(), new Bracewell(),
            new CerhaHempel(), new Cobalt(), new ControlRisks(), new Cooley(), new Cuatrecasas(),
            new Curtis(), new Dentons(), new Ellex(), new EmploymentLawAlliance(), new EProint(),
            new FangdaPartners(), new GuantaoLaw(), new HiggsAndJohnson(), new Houthoof(), new JohnsonCamachoAndSingh(),
            new JPMAndPartners(), new KingAndWoodMallesons(), new LatamLex(), new Legance(), new LewissSilkin(),
            new LexCaribbean(), new Luther(), new MarksAndClerk(), new MdME(), new METIDA(),
            new MIOLaw(), new Noerr(), new OsborneClarke(), new PearlCohen(), new PortolanoCavallo(),
            new Pulegal(), new QuinEmanuel(), new ReinholdCohnGroup(), new RiadSalehAndPartners(), new SabaAndCo(),
            new SdzlegalSchindhelm(), new SimmonsAndSimmons(), new Sorainen(), new StephensonHarwood(), new SullivanAndCromwell(),
            new TEMPLARS(), new Thommessen(), new Vaneps(), new WALLESS(), new WithersKhattarWong(),
    };

    private static final Site[] TEST = {
// ByNewPage - Europe
            new GPAAdvogados(), new LundElmerSandager(), new LXA(), new MadirazzaPartners(),
            new Monard(), new MPLegal(), new SPSAdvogados(),

// ByNewPage
// North America
            new AcedoSantamarin(), new BCB(), new CervantesAbogados(), new Chaitons(), new CoxAndPalmer(),
            new Creel(), new CurtisDawe(), new DeHoyosAviles(), new DunnCox(), new FishmanFlanzMelandPaquin(),
            new Harris(), new KanukaThuringer(), new Lavery(), new Lerners(), new MalpicaIturbeBujParedes(),
            new MGGL(), new PerezCorreaGonzalez(), new RGRH(), new RoblesMiaja(), new SIERRALatam(),
            new VilaAbogados(),

// South America
            new DanielLaw(), new DSNavarroCastex(), new FitzwilliamStone(), new GumucioAbogados(), new MadrugaBTW(),
            new MGDalyPartners(), new MirandaAmado(), new MitraniCaballero(), new MolinaRios(), new NovotnyAdvogados(),
            new Osterling(), new PalaciosLleras(), new PSTBN(), new PugaOrtiz(), new RiedFabres(),
            new Robalino(), new RojasLawFirm(), new RomeuAmaralAdvogados(), new RosselloAbogados(), new SuarezConsultoria(),
            new TorresPlazAraujo(), new WilkinsonGrist(),
    };

    // ==================== GETTERS BY CONTINENT ====================

    public static Site[] getAfrica()   { return AFRICA; }
    public static Site[] getAsia()     { return ASIA; }
    public static Site[] getEurope()   { return EUROPE; }
    public static Site[] getAmericas() { return AMERICAS; }
    public static Site[] getOceania()  { return OCEANIA; }
    public static Site[] getMundial()  { return MUNDIAL; }

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
        if (ContinentConfig.isContinentEnabled("Americas")) sites.addAll(Arrays.asList(AMERICAS));
        if (ContinentConfig.isContinentEnabled("Oceania")) sites.addAll(Arrays.asList(OCEANIA));

        // Mundial is always included (global firms)
        sites.addAll(Arrays.asList(MUNDIAL));

        return sites.toArray(new Site[0]);
    }
}