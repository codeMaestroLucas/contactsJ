package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.to_test.americas.*;
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
            new ENRAdvisory(), new ENSAfrica(), new FatimaFreitas(), new FBLAdvogados(), new GillGodlontonAndGerrans(),
            new HansOffiaAndAssociates(), new HJW(), new IbrachyAndDermarkar(), new IkeyiShittuAndCo(), new JacksonEttiAndEdu(),
            new JLDAndMBLegal(), new KOAssociates(), new MMAKS(), new MMAN(), new MulengaMundashiLegalPractitioners(),
            new MusibauAdetunbiAndCo(), new MuvingiAndMugadza(), new OraroAndCompany(), new Ortus(), new PerchstoneAndGraeys(),
            new RDSPartners(), new RobinsonBertram(), new RONNLaw(), new SenekalSimmonds(), new Shalakany(),
            new Shalakany(), new SLKB(), new TNP(), new UdoUdomaBeloOsagie(), new Werksmans(),
            new ZakiHashemAndPartners(), new ZulficarAndPartners(),
            new ABLPartnersLP(), new AJUMOGOBIAOKEKE(), new ALN(), new BanwoIghodalo(),
            new EldibAndCo(), new JKGadzamaLLP(), new MboyaWangonguWaiyaki(), new OlaniwunAjayi(),
            new OlisaAgbakobaLegal(), new PacSolicitors(), new Punuka(), new RosenutSolicitors(), new Tabacks(),
            new TopeAdebayoLP(),
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
            new GladeMichelWirtz(), new GLXLTM(), new GORG(), new GPAAdvogados(), new Granrut(),
            new GreeneAndGreeneSolicitors(), new GrigorescuStefanica(), new GrimaldiAlliance(), new GunAndPartners(), new GuttOlkFeldhaus(),
            new GVZH(), new Hamso(), new HarteBavendamm(), new HaslingerNagele(), new Hayes(),
            new HoffmannEitle(), new Holmes(), new Horten(), new HoxhaMemiAndHoxha(), new HWWHermannWienbergWilhelm(),
            new IdEstAvocats(), new IlejAndPartners(), new Interlaw(), new isarpatentMunchen(), new JadekAndPensa(),
            new JBLaw(), new JWP(), new Kaimakliotis(), new KallioLaw(), new KambourovAndPartners(),
            new KBVLLaw(), new KennedyVanderLaan(), new KeystoneLaw(), new KLCLaw(), new Kneppelhout(),
            new KolcuogluDemirkanKocakli(), new Kondrat(), new KonecnaAndZacha(), new Kvale(), new KWKRLaw(),
            new LacourteRaquinTatar(), new LAGRO(), new LambadariosLaw(), new Landslog(), new LangsethAdvokat(),
            new Legalis(), new Legance(), new LeitnerLawRechtsanwalte(), new Lener(), new LenzAndStaehelin(),
            new LePooleBekema(), new Lexence(), new Lindahl(), new LindemannSchwennickePartner(), new LLBerg(),
            new LMCR(), new LMS(), new LSWF(), new LSWLaw(), new LundElmerSandager(),
            new LXA(), new MacedoVitorino(), new MadirazzaPartners(), new MaikowskiAndNinnemann(), new MalagaSolicitors(),
            new Maples(), new Mariscal(), new MENACityLawyers(), new MermozAvocats(), new MeyerKoring(),
            new MFWFialek(), new MihajIlicAndMilanovic(), new Miranda(), new MishcondeReya(), new MLLLegal(),
            new Monard(), new MorrisLaw(), new MPLegal(), new MPRPartners(), new MSBSolicitors(),
            new NagyTrocsanyi(), new NESTOR(), new NGA(), new NGLLegal(), new NicolasKanellopoulos(),
            new NiedererKraftFrey(), new NiederhuberAndPartner(), new OgletreeDeakins(), new OneEssexCourt(), new Onsagers(),
            new ONVLaw(), new Oppenheim(), new Oppenhoff(), new Orsingher(), new Oxera(),
            new PapapolitisAndPapapolitis(), new Pbbr(), new PeliPartners(), new PepeljugoskiLaw(), new Pestalozzi(),
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
            new SPSAdvogados(), new SRSLegal(), new StehlinAssocies(), new Strelia(), new SZA(),
            new Szecskay(), new UriaMenendez(), new Valfor(), new VanOlmenAndWynant(), new Vischer(),
            new VisionConsulting(), new VossiusAndPartner(), new WardynskiAndPartners(), new WengerVieliAG(), new Wiersholm(),
            new WierzbowskiAndPartners(), new WikborgRein(), new WindtLeGrand(), new ZampaPartners(), new ZeposAndYannopoulos(),
            new HoffmannLiebs(), new Homburger(), new NetCraman(), new TEGOS(), new TRINITILawFirm(),
            new EdwinCoe(), new HVGLaw(), new IoannidesDemetriouLLC(), new Jeantet(), new JonssonAndHall(),
            new KaratzasAndPartners(), new LakatosKovesPartners(),
    };

    private static final Site[] AMERICAS = {
// North America
            new AGMAbogados(), new ArthurCox(), new AsafoAndCo(), new BCB(), new BCFLaw(),
            new BensonBuffett(), new BoyneClarke(), new BWBLLP(), new CervantesAbogados(), new Chaitons(),
            new CoxAndPalmer(), new Creel(), new CurtisDawe(), new DeHoyosAviles(), new DunnCox(),
            new FilionWakelyThorupAngeletti(), new FishmanFlanzMelandPaquin(), new Galicia(), new Harris(), new IbanezParkman(),
            new JimenezPena(), new KanukaThuringer(), new KuriBrena(), new Lavery(), new Leaf(),
            new Lerners(), new LivingstonAlexanderAndLevy(), new MalpicaIturbeBujParedes(), new MBM(), new McKinneyBancroftAndHughes(),
            new MedinaGarnesAbogados(), new MGGL(), new NaderHayauxAndGoebel(), new NunesScholefieldDeLeonAndCo(), new PattersonMairHamilton(),
            new PerezCorreaGonzalez(), new PietrantoniMendezAndAlvarezLLC(), new Pitblado(), new PrasadAndCompany(), new RGRH(),
            new RoblesMiaja(), new Sangra(), new SangraMollerLLP(), new SIERRALatam(), new ThompsonDorfmanSweatman(),
            new VazquezTerceroAndZepeda(), new VilaAbogados(),

// Central America
            new AlburquerqueAbogados(), new Alcogal(), new DeCampsVasquezVarela(), new FabregaMolinoMulino(), new GuzmanAriza(),
            new IcazaGonzalezRuizAndAleman(), new Lovill(), new MorganAndMorgan(), new MyersFletcherAndGordon(),

// South America
            new Abe(), new AlbagliZaliasnik(), new AlessandriLawyers(), new Amprimo(), new Aninat(),
            new BaleraBerbelMitne(), new BAQSN(), new BaraonaMarshall(), new BaschRameh(), new BBGS(),
            new BicalhoNavarro(), new BicharaEMotta(), new BofillMir(), new Bomchil(), new Bragard(),
            new BrigrardUrrutia(), new BRZ(), new Carey(), new CariolaDiezPerezCotapos(), new Cassagne(),
            new CBLM(), new Chediak(), new ChrupoEvans(), new ClaroYCia(), new CoronelPerez(),
            new CPB(), new CrialesUrcullo(), new CTPAdvogados(), new Damma(), new DiBlasiParente(),
            new DuarteGarcia(), new ErnestoBorges(), new EstudioOlaechea(), new FerradaNehme(), new Ferrere(),
            new FischerCia(), new FitzwilliamStone(), new FMDerraik(), new GaiaSilvaGaedeAndAssociados(), new GranadeiroGuimaraes(),
            new GuerreroOlivos(), new GumucioAbogados(), new GuyerRegules(), new HDLegal(), new HopeDugganSilva(),
            new IWMelcheds(), new JArmandoBatista(), new JDSellierAndCo(), new JustenPereira(), new KVLAW(),
            new LabbeAbogados(), new LatinAlliance(), new LavAbogadosConsultores(), new Lembeye(), new LexvalorAbogados(),
            new LoboDeRizzo(), new Madrona(), new MadrugaBTW(), new MelloTorres(), new MHRLegal(),
            new MirandaAmado(), new MitraniCaballero(), new MolinaRios(), new MoralesYBesa(), new MorenoBaldivieso(),
            new MottaFernandes(), new MPATradeLaw(), new MundieEAdvogados(), new NFA(), new NHM(),
            new NoboaPenaTorres(), new NovotnyAdvogados(), new OFarrell(), new Olaechea(), new Osterling(),
            new PeixotoCury(), new PGLaw(), new PHRLegal(), new PNMAdovagos(), new PradoVidigal(),
            new Prieto(), new PSTBN(), new PugaOrtiz(), new RiedFabres(), new RMADVAdvogados(),
            new RodriguezAngobaldo(), new RomeuAmaralAdvogados(), new RosselloAbogados(), new RossiMaffiniMilmanGrando(), new SachaCalmon(),
            new Santivanez(), new SantosBevilaquaAdvogados(), new SargentAndKrahn(), new Silva(), new SiqueiraCastro(),
            new TAGD(), new TrenchRossiWatanabe(), new VBDAdvogados(), new VBSOAdvogados(), new Velloza(),
            new VieiraRezendeAdvogados(), new WilliamFreireAdvogados(), new WongtschowskiKleimanAdvogados(), new ZBV(), new ZuzunagaAssereto(),
    };

    private static final Site[] OCEANIA = {
            new AitkenPartners(), new AJLawAndCo(), new Baumgartners(), new Chamberlains(), new ConnollySuthers(),
            new Corcoran(), new DeutschMiller(), new DWFoxTucker(), new Finlaysons(), new Gadens(),
            new GilbertAndTobin(), new GreenwoodRoche(), new Grette(), new HamiltonLocke(), new HeskethHenry(),
            new Hicksons(), new HWEbsworth(), new Madderns(), new MatthewsFolbigg(), new McCulloughRobertson(),
            new MellorOlsson(), new NormanWaterhouse(), new RussellMcVeagh(), new SimmonsWolfhagen(), new SimpsonGrierson(),
            new TompkinsWake(), new WengerVieliAG(), new WilsonHarle(), new WilsonRyanGrose(), new Wrays(),
            new YoungList(), new GriffithHack(),
    };

    private static final Site[] MUNDIAL = {
            new Adna(), new ALGoodbody(), new ArochiLindner(), new BDO(), new Bracewell(),
            new CerhaHempel(), new Cobalt(), new ControlRisks(), new Cooley(), new Cuatrecasas(),
            new Curtis(), new Dentons(), new DSNavarroCastex(), new Ellex(), new EmploymentLawAlliance(),
            new EProint(), new FangdaPartners(), new GuantaoLaw(), new HiggsAndJohnson(), new Houthoof(),
            new JohnsonCamachoAndSingh(), new JPMAndPartners(), new KingAndWoodMallesons(), new LatamLex(), new Legance(),
            new LewissSilkin(), new LexCaribbean(), new Littler(), new Luther(), new MarksAndClerk(),
            new MdME(), new METIDA(), new MIOLaw(), new Noerr(), new OsborneClarke(),
            new PearlCohen(), new PortolanoCavallo(), new Pulegal(), new QuinEmanuel(), new ReinholdCohnGroup(),
            new RiadSalehAndPartners(), new SabaAndCo(), new SdzlegalSchindhelm(), new SimmonsAndSimmons(), new Sorainen(),
            new StephensonHarwood(), new SullivanAndCromwell(), new TEMPLARS(), new Thommessen(), new Vaneps(),
            new WALLESS(), new WithersKhattarWong(), new Clyde(), new Garrigues(), new Trowers(),
    };

    private static final Site[] TEST = {
// ByPage - Americas
// South America
            new AraujoPolicastro(), new AzevedoSette(), new FCGD(), new LevySalomao(), new LOBaptista(),
            new StoccheForbes(),

// ByNewPage - Americas
// Central America
            new GuerraGonzalez(),

// South America
            new BronsSalas(), new Cascione(), new CRFRojas(), new DannemannSiemsen(), new DHC(),
            new DiasCarneiro(), new EstudioBunge(), new FreitasLeite(), new GBreuer(), new GusmaoELabrunie(),
            new HDS(), new KLA(), new NicholsonCano(), new RCTZZ(), new Severgnini(),
            new TozziniFreire(), new VidigalNeto(),
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