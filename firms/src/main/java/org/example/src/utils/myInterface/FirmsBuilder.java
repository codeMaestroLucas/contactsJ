package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.africa.*;
import org.example.src.sites.americas.*;
import org.example.src.sites.asia.*;
import org.example.src.sites.europe.*;
import org.example.src.sites.mundial.*;
import org.example.src.sites.oceania.*;
import org.example.src.sites.to_test.americas.*;
import org.example.src.utils.ContinentConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unified builder for all firms, organized by continent.
 * Replaces the former ByPageFirmsBuilder and ByNewPageFirmsBuilder.
 * Firms are grouped by continent;
 */
public class FirmsBuilder {

    private static final Site[] AFRICA = {
            new ABLPartnersLP(), new AbrahamsAndGrossIncorporated(), new AdamsAndAdams(), new Adsero(), new AFMpanga(),
            new AfricaseAttorneys(), new AJUMOGOBIAOKEKE(), new ALCAlieldeanWeshahi(), new AllianceLaw(), new ALN(),
            new ALPNGAndCo(), new AlukoAndOyebode(), new AmanAndPartners(), new Ashitiva(), new AshongBenjamin(),
            new AVM(), new BanwoIghodalo(), new BarnardInc(), new BennaniAndAssocies(), new BentsiEnchillLetsaAndAnkomah(),
            new BirungyiBarataAndAssociates(), new BLCRobertAndAssociatesLtd(), new Bookbinder(), new Bowmans(), new ChazaiWamba(),
            new CliffeDekkerHofmeyr(), new CorpusLegal(), new CoxYeats(), new DualeOviaAlexAdedipe(), new EldibAdvocates(),
            new EldibAndCo(), new EnglingStritterAndPartners(), new EngoruMutebi(), new ENRAdvisory(), new ENSAfrica(),
            new FatimaFreitas(), new FBLAdvogados(), new FisherQuarmbyAndPfeifer(), new GillGodlontonAndGerrans(), new HansOffiaAndAssociates(),
            new HJW(), new IbrachyAndDermarkar(), new IkeyiShittuAndCo(), new JacksonEttiAndEdu(), new JKGadzamaLLP(),
            new JLDAndMBLegal(), new KantorAndImmerman(), new Kenna(), new KOAssociates(), new MboyaWangonguWaiyaki(),
            new MMAKS(), new MMAN(), new MulengaMundashiLegalPractitioners(), new MusibauAdetunbiAndCo(), new MuvingiAndMugadza(),
            new OjukwuFaotuYusuf(), new OlaniwunAjayi(), new OlisaAgbakobaLegal(), new OraroAndCompany(), new Ortus(),
            new PacSolicitors(), new PerchstoneAndGraeys(), new Punuka(), new RDSPartners(), new RitzAttorneysAtLaw(),
            new RobinsonBertram(), new RONNLaw(), new RosenutSolicitors(), new SenekalSimmonds(), new ShahidLaw(),
            new Shalakany(), new Shalakany(), new SLKB(), new SPAAjibadeAndCo(), new SpoorAndFisher(),
            new STBB(), new Tabacks(), new TheartMey(), new TNP(), new TopeAdebayoLP(),
            new TripleOKLaw(), new UdoUdomaBeloOsagie(), new UkiriLijadu(), new Werksmans(), new ZakiHashemAndPartners(),
            new ZulficarAndPartners(),
    };

    private static final Site[] ASIA = {
            new AbeIkuboAndKatayama(), new ABNR(), new ABSAndCo(), new ACCRALAW(), new ACIP(),
            new ADCOLaw(), new AdnanSundraAndLowNP(), new AdnanSundraAndLowP(), new AECO(), new Aequitas(),
            new AgmonWithTulchinsky(), new AJUKimChangAndLee(), new AlAlawiCo(), new AlBusaidyMansoorJamal(), new AlHamadLegal(),
            new AllBrightLaw(), new AllenAndGledhill(), new AlMarkazLaw(), new ALMTLegal(), new AlYaqoutAlFouzan(),
            new Amicus(), new AMTLaw(), new AnandAndAnand(), new AnhadLaw(), new AnJieBroad(),
            new Anli(), new ANSS(), new AOil(), new AquinasLawAlliance(), new ArcheusLaw(),
            new ArethaLegal(), new Argus(), new AronTadmorLevy(), new ASAndHCliffordChance(), new ASAR(),
            new AscendantLegal(), new AsiaLiuhIP(), new AssegafHamzahAndPartners(), new AtlasLaw(), new AtsumiAndSakai(),
            new AxisLawChambers(), new AYMP(), new AZB(), new AzmanDavidsonAndCo(), new BaeKimAndLee(),
            new BalterGuthAloniAndCo(), new BaohuaLaw(), new BarneaAndCo(), new BeijingEastIP(), new BhandariNaqviRiaz(),
            new BharuchaAndPartners(), new BLCASSOCIATES(), new BoaseCohenAndCollins(), new BossYoung(), new Bowers(),
            new BraunPartners(), new BrossAndPartners(), new BSALaw(), new BTGAdvaya(), new BunAssociates(),
            new CapitalEquityLegalGroup(), new CC(), new CFNLaw(), new CG(), new ChadhaAndCo(),
            new ChanceBridge(), new ChanceBridge(), new ChandlerMHM(), new ChangTsiAndPartners(), new ChienYehLaw(),
            new ChuoSogoLPC(), new CityYuwaPartners(), new ClasisLaw(), new CNPLaw(), new CoEffortLaw(),
            new CollyerLaw(), new CorneliusLaneAndMufti(), new CovenantChambers(), new CTPartners(), new CyrilAmarchandMangaldas(),
            new DaiichiFuyo(), new Dashnyam(), new DavidsonAndCo(), new Deacons(), new DehehengLaw(),
            new DeHeng(), new DesaiAndDiwanji(), new DFDL(), new DhavalVussonjiAndAssociates(), new DhirAndDhir(),
            new DLAndFDeSaram(), new Docvit(), new DomnernSomgiatAndBoonma(), new DonaldsonAndBurkinshaw(), new DowwayAndPartners(),
            new DRAndAJU(), new DrewAndNapier(), new DSKLegal(), new DuanDuan(), new DuanDuan(),
            new EastAndConcord(), new EBN(), new EconomicLawsPractice(), new EJELaw(), new EldanLaw(),
            new EligGurkaynak(), new ErdemAndErdem(), new ErgunAvukatlikBurosu(), new ERM(), new FCLaw(),
            new FGEEbrahimHosain(), new FironLaw(), new FirstLawPC(), new Fischer(), new FJAndGDeSaram(),
            new FKNKLaw(), new FoongAndPartners(), new FoxAndMandal(), new Frasers(), new Gall(),
            new Gallant(), new GanLeeAndTan(), new GKCPartners(), new GLAAndCompany(), new GlobalLawOffice(),
            new GlobalVietnamLawyers(), new GornitzkyAndCo(), new GrandwayLaw(), new GrossAndCo(), new GRUBALAW(),
            new GulapaLaw(), new GuoyaoQindaoLaw(), new HaidermotaAndCo(), new HaiRun(), new HaiRun(),
            new Haiwen(), new Haiwen(), new HalimHongAndQuek(), new HarryElias(), new HastingsAndCo(),
            new Hauzen(), new Helmsman(), new HFAndCo(), new HHRLawyers(), new HiswaraBunjaminAndTandjung(),
            new Hiways(), new HornAndCo(), new HowseWilliams(), new HugillAndIp(), new HuiyeLaw(),
            new HuiZhong(), new HuiZhong(), new Hylands(), new Hylands(), new HYLeungAndCo(),
            new IndiaLawOffices(), new JeffLeongPoonAndWong(), new JiaYuanLaw(), new JinchengTongdaAndNeal(), new JingtianGongcheng(),
            new JingtianGongcheng(), new JIPYONG(), new JointWin(), new JPMLaw(), new JTJBInternationalLawyers(),
            new JunZeJunLaw(), new JuslawsAndConsult(), new K1Chamber(), new KaiRongLaw(), new KanKrishme(),
            new KECOLegal(), new KhaitanAndCo(), new KimChangAndLee(), new KingStubbAndKasiva(), new KLaw(),
            new KochharAndCo(), new KojimaLaw(), new KRBLaw(), new LanbaiLaw(), new LAWPartnership(),
            new LeeAndKo(), new LeeAndLee(), new LeeInternational(), new LeeTsaiAndPartners(), new LegacyLaw(),
            new Lektou(), new LepaMeirAndCo(), new LHAG(), new Llinks(), new LNT(),
            new LonganLaw(), new LSHorizon(), new LuthraAndLuthra(), new MachasAndPartners(), new MaheshwariAndCo(),
            new MakesAndPartners(), new ManuelaAntonio(), new MAQLegal(), new MASLaw(), new MatryMeiriAndCo(),
            new MDLaw(), new MDPAndPartners(), new MeitarLaw(), new Meysan(), new MishconKaras(),
            new MiuraAndPartners(), new MochtarKaruwinKomar(), new MohanadassPartnership(), new MohsinTayebalyAndCo(), new MomoOMatsuoAndNamba(),
            new MoriHamadaAndMatsumoto(), new MorogluArseven(), new MosveldttLaw(), new Mourant(), new MVGS(),
            new MZMLegal(), new NagashimaOhnoAndTsunematsu(), new NumenLaw(), new NunoSimoesAndAssociados(), new NurmansyahAndMuzdalifah(),
            new OcampoAndSuralvo(), new OhEbashiLPCAndPartners(), new OldhamLiAndNie(), new ONC(), new OneAsiaLawyers(),
            new OonBazul(), new Paksoy(), new PhoenixLegal(), new PKWongAndNair(), new PlatonMartinez(),
            new Poovayya(), new PremierChambers(), new ProvidenceLaw(), new PSL(), new PunoLaw(),
            new QuaheWooAndPalmer(), new QuiasonMakalintal(), new RahmatLimAndPartners(), new RajaniAssociates(), new RamdasAndWong(),
            new RCLChambersLaw(), new RemfryAndSagar(), new RHTLaw(), new RIAABarkerGillette(), new RiverDeltaLaw(),
            new RobertsonsSolicitors(), new RomuloLawFirm(), new RP(), new RSM(), new SagaLegal(),
            new SamvadPartners(), new SarafAndPartners(), new SASLO(), new SayatZholshyAndPartners(), new SCPT(),
            new SEUM(), new SFKSLaw(), new SGAndCoLawyers(), new SGLALaw(), new ShandongDehengLaw(),
            new ShanghaiPacificLegal(), new ShardulAmarchandMangaldasAndCo(), new ShiboletAndCo(), new ShinAndKim(), new ShoobAndCo(),
            new ShookLinAndBokNP(), new ShookLinAndBokP(), new ShookLinBok(), new SHorowitzAndCo(), new SIGNUM(),
            new SinghaniaAndPartners(), new SIPLaw(), new Skrine(), new SNRAssociates(), new SoemadipradjaAndTaher(),
            new SoemadipradjaTaher(), new SOIP(), new SokSiphanaAndAssociates(), new SolomonAndCo(), new SteinmetzHaringGurman(),
            new Stellex(), new StevensonWongAndCo(), new StratageLaw(), new SudathPerera(), new TALaw(),
            new TANLaw(), new TannerDeWitt(), new TCLaw(), new TheCapitalLaw(), new TianYuan(),
            new TillekeGibbins(), new TiruchelvamAssociates(), new TMIAssociates(), new TMPIntellectualProperty(), new TokyoInternationalLaw(),
            new TommyThomas(), new Tongshang(), new Trilegal(), new TsarAndTsai(), new TSMP(),
            new TTA(), new TTTAndPartners(), new UMBRA(), new UshijimaAndPartners(), new Vaish(),
            new VALaw(), new VanguardLawyersTokyo(), new VellaniAndVellani(), new VeritasLegal(), new VILAF(),
            new Virtus(), new VisionAndAssociates(), new WangJingAndCo(), new WilliamHendrikSiregarDjojonegoro(), new WongPartnership(),
            new WooKwanLeeAndLo(), new YKVN(), new YoonAndYang(), new YossiLevyAndCo(), new YouMe(),
            new YuenLaw(), new Yulchon(), new YYCLegal(), new ZaidIbrahimAndCo(), new ZanHub(),
            new ZhongLun(), new ZhongLunLaw(), new ZhongziLaw(),
    };

    private static final Site[] EUROPE = {
            new ABGIP(), new ACAndR(), new ACTLEGAL(), new ADVANTAltana(), new ADVANTBeiten(),
            new Aera(), new AgioLegal(), new AGPAdvokater(), new Alliotts(), new AlstonAndBirdLLP(),
            new AMERELLER(), new Amorys(), new AndersenTaxLegalIberia(), new AraozAndRueda(), new ArendtMedernach(),
            new ArnesenIP(), new ArnoldAndSiedsma(), new ARQUIS(), new ASCHukuk(), new ASPapadimitriouPartners(),
            new Astrea(), new AtlasAdvokater(), new AugustDebouzy(), new AumentoLawFirm(), new Avance(),
            new AVSLegal(), new B2RLaw(), new BadriAndSalimElMeouchiLaw(), new BAHR(), new BancilaDiaconuSiAsociatii(),
            new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new BARGERPREKOP(), new BDGSAssociates(), new BDKAdvokati(),
            new Beauchamps(), new BechBruun(), new Belgravia(), new Bener(), new Berggren(),
            new Bernitsas(), new BettenAndResch(), new BGLegal(), new BlakeMorgan(), new BlandyAndBlandy(),
            new BonelliErede(), new BonnAndSchmitt(), new BoodleHatfield(), new Borenius(), new Boyanov(),
            new BrandlTalos(), new BRAUNEISRECHTSANWALTE(), new BrinkmannAndPartner(), new Broseta(), new BrownRudnick(),
            new BSJP(), new BulboacaAsociatii(), new BullAndCo(), new BureauPlattner(), new BUREN(),
            new BurgesSalmon(), new BussMurtonLaw(), new ByrneWallace(), new BYRO(), new CAINS(),
            new CamilleriPreziosi(), new CampbellsLegal(), new CarneluttiLaw(), new CasesLacambra(), new CastrenAndSnellman(),
            new CBA(), new CCALegal(), new CCSLegal(), new Cerraloglu(), new ChryssesDemetriades(),
            new Cirio(), new Clarkslegal(), new ClemensLaw(), new CLPLaw(), new Codex(),
            new CollasCrill(), new Contrast(), new CravathSwaineAndMoore(), new CRCCD(), new CWAAssociates(),
            new DahlLaw(), new DALDEWOLF(), new DANUBIAPatentAndLaw(), new Darrois(), new DavisPolkAndWardwell(),
            new DechertLLP(), new DeClercq(), new DeGaulleFleurance(), new Delcade(), new DePardieu(),
            new Desfilis(), new DGKV(), new DimitrovPetrovAndCo(), new DinovaRusevAndPartners(), new DKCO(),
            new DMSLegal(), new Dottir(), new DrzewieckiTomaszek(), new DSMAvocats(), new DZPLaw(),
            new EdwinCoe(), new EisenfuhrSpeiserAndPartner(), new Ekelmans(), new EldibAdvocates(), new EliasNeocleous(),
            new EllisonsSolicitors(), new Elverdam(), new EPAndC(), new ErsoyBilgehan(), new Esche(),
            new Esin(), new Eubelius(), new EuclidLaw(), new EvershedsSutherland(), new FCMLimited(),
            new Fidal(), new FilipAndCompany(), new Finnius(), new Finreg360(), new FIVERS(),
            new FlichyGrange(), new FluegelPreissner(), new FortLegal(), new Foyen(), new FPSLaw(),
            new FrancisWilksAndJones(), new FranklinLaw(), new Frontier(), new FTPA(), new Fylgia(),
            new GanadoAdvocates(), new GElíasMuñoz(), new GeorgievTodorovAndCo(), new GilibertiTriscorniaEAssociati(), new GittiAndPartners(),
            new GladeMichelWirtz(), new GleissLutz(), new GLXLTM(), new GORG(), new GorrissenFederspiel(),
            new GPAAdvogados(), new GPK(), new Granrut(), new GrataInternational(), new GreeneAndGreeneSolicitors(),
            new GreenHorseLegal(), new GrigorescuStefanica(), new GrimaldiAlliance(), new GunAndPartners(), new GuttOlkFeldhaus(),
            new GvWGrafvonWestphalen(), new GVZH(), new Haavind(), new HabrakenRutten(), new HammarskioldAndCo(),
            new Hamso(), new HannesSnellman(), new HarperJamesSolicitors(), new HarteBavendamm(), new HaslingerNagele(),
            new Hayes(), new HCRLegal(), new Hedman(), new Hellstrom(), new HjulmandCaptain(),
            new HoffmannEitle(), new HoffmannLiebs(), new Holmes(), new Holst(), new Homburger(),
            new Horten(), new HoxhaMemiAndHoxha(), new HPPAttorneys(), new Hugel(), new HVGLaw(),
            new HWWHermannWienbergWilhelm(), new IdEstAvocats(), new IlejAndPartners(), new Interlaw(), new IoannidesDemetriouLLC(),
            new isarpatentMunchen(), new JadekAndPensa(), new Jalsovszky(), new JBLaw(), new Jeantet(),
            new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new JonssonAndHall(), new JWP(),
            new Kaimakliotis(), new Kallan(), new KallioLaw(), new KambourovAndPartners(), new Kanter(),
            new KaratzasAndPartners(), new KBVLLaw(), new KCGPartners(), new KennedyVanderLaan(), new KewLaw(),
            new KeystoneLaw(), new KienhuisLegal(), new KLCLaw(), new Kneppelhout(), new KnezovicAndAssociates(),
            new Knijff(), new KochanskiAndPartners(), new KolcuogluDemirkanKocakli(), new Kolster(), new Kondrat(),
            new KonecnaAndZacha(), new Krogerus(), new KromannReumert(), new Kvale(), new KWKRLaw(),
            new LacourteRaquinTatar(), new LAGRO(), new LakatosKovesPartners(), new LambadariosLaw(), new Landslog(),
            new LangsethAdvokat(), new LaszczukAndWspolnicy(), new Legalis(), new Legance(), new LeitnerLawRechtsanwalte(),
            new LemstraVanDerKorst(), new Lener(), new LenzAndStaehelin(), new LePooleBekema(), new Lexence(),
            new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Lindahl(), new LindemannSchwennickePartner(),
            new LLBerg(), new LMCR(), new LMS(), new Logos(), new LPAGGV(),
            new LSWF(), new LSWLaw(), new LundElmerSandager(), new LXA(), new Lydian(),
            new LYND(), new MacedoVitorino(), new MadirazzaPartners(), new MaikowskiAndNinnemann(), new MalagaSolicitors(),
            new MaleckiLegal(), new Maples(), new MAQS(), new Mariscal(), new Matheson(),
            new MazantiAndersen(), new MccannFitzGerald(), new McDermottWillAndEmery(), new MellingVoitishkinAndPartners(), new MENACityLawyers(),
            new MerilampiAttorneys(), new MermozAvocats(), new MeyerKoring(), new MFWFialek(), new MihajIlicAndMilanovic(),
            new Miranda(), new MishcondeReya(), new MitelAndAsociatii(), new MLLLegal(), new MMDAdvokati(),
            new MoalemWeitemeyer(), new Molinari(), new Monard(), new MooreLegalKovacs(), new MorrisLaw(),
            new MPLegal(), new MPRPartners(), new MSBSolicitors(), new MSP(), new MusatAsociatii(),
            new MVJMarkovicVukoticJovkovic(), new MVVPAdvocaten(), new NagyTrocsanyi(), new NaschitzBrandesAmir(), new NESTOR(),
            new NetCraman(), new NGA(), new NGLLegal(), new NicolasKanellopoulos(), new NiedererKraftFrey(),
            new NiederhuberAndPartner(), new Niedermüller(), new NielsenNorager(), new Njord(), new NOEWE(),
            new Norens(), new NovaLaw(), new NPPLegal(), new NunzianteMagrone(), new NysinghAdvocatenNotarissenNV(),
            new Odigo(), new OgletreeDeakins(), new OneEssexCourt(), new Onsagers(), new ONVLaw(),
            new Oppenheim(), new Oppenhoff(), new Orrick(), new Orsingher(), new Oxera(),
            new PanettaConsultingGroup(), new PapapolitisAndPapapolitis(), new PatrikiosPavlouAndAssociates(), new PayetReyCauviPerez(), new Pbbr(),
            new Pedersoli(), new PeliPartners(), new Penta(), new PepeljugoskiLaw(), new Pestalozzi(),
            new PFPLaw(), new PHH(), new PhilipLee(), new PhilippeAndPartners(), new PiniFrancoLLP(),
            new PinneyTalfourdSolicitors(), new Plesner(), new PLMJ(), new Ploum(), new PMP(),
            new POELLATH(), new PolitisPartners(), new PopoviciNituStoicaAndAsociatii(), new PortaAndConsulentiAssociati(), new PorwiszAndPartners(),
            new PotamitisVekris(), new Poulschmith(), new PragerDreifuss(), new PricaAndPartners(), new PrinzAndPartner(),
            new ProskauerRose(), new ProvencaDeCarvalho(), new PrueferAndPartner(), new PuschWahlig(), new Quinz(),
            new Racine(), new RadovanovicStojanovic(), new RadulescuAndMusoi(), new RambergAdvokater(), new Rask(),
            new RBK(), new RBLaw(), new ReinholdCohnGroup(), new RKKW(), new RocaJunyent(),
            new RoedlAndPartner(), new RojsPeljhan(), new Roschier(), new RPCLegal(), new RTPR(),
            new RutgersPosch(), new RuzickaPartners(), new RymarzZdortMaruta(), new SadkowskiWspolnicy(), new SalusLegal(),
            new SampsonCowardLLP(), new SANDS(), new SayinLaw(), new SBGK(), new SchalastAndPartner(),
            new SchellenbergWittmer(), new SchindlerAttorneys(), new Schjodt(), new SchneeweissWeixelbaum(), new Schoups(),
            new SchrammOehler(), new SchurtiPartners(), new ScottoPartners(), new SelihAndPartnerji(), new Selmer(),
            new SenicaPartners(), new ServuloAndAssociados(), new Setterwalls(), new SGP(), new Sherrards(),
            new SibincicNovakPartners(), new Sidley(), new SimonAssocies(), new SimontBraun(), new SIRIUS(),
            new SkauReipurth(), new SkilsLaw(), new SkrastinsDzenis(), new SKWSchwarz(), new SlaughterAndMay(),
            new Snellman(), new SokolNovakTrojanDolecek(), new SouriadakisTsibris(), new SPSAdvogados(), new SRSLegal(),
            new StehlinAssocies(), new Stibbe(), new StoneKing(), new Strelia(), new SuarezDeVivero(),
            new SZA(), new Szecskay(), new TEGOS(), new ThomasBodstrom(), new Titov(),
            new TRINITILawFirm(), new TucaZbarcea(), new UriaMenendez(), new Valfor(), new VanDerPutt(),
            new VanOlmenAndWynant(), new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(), new Vischer(),
            new VisionConsulting(), new VOPatentsAndTrademarks(), new VossiusAndPartner(), new WardynskiAndPartners(), new WengerVieliAG(),
            new WIDEN(), new Wiersholm(), new WierzbowskiAndPartners(), new WikborgRein(), new WindtLeGrand(),
            new WolfTheiss(), new ZamfirescuRacotiPredoiu(), new ZampaPartners(), new ZeposAndYannopoulos(),
    };

    private static final Site[] AMERICAS = {

            // North America
            new AGMAbogados(), new ArthurCox(), new AsafoAndCo(), new BarristonLaw(), new BCB(),
            new BCFLaw(), new BennettJones(), new BensonBuffett(), new BLGLaw(), new BoyneClarke(),
            new BurnetDuckworthAndPalmer(), new BWBLLP(), new Cassels(), new CervantesAbogados(), new Chaitons(),
            new Chanis(), new ClarkWilson(), new CoxAndPalmer(), new CozenOConnor(), new Creel(),
            new CurtisDawe(), new DaleAndLessmann(), new DeethWilliamsWall(), new DeHoyosAviles(), new DillonEustace(),
            new DunnCox(), new ECLegalRubio(), new Farris(), new FilionWakelyThorupAngeletti(), new FillmoreRiley(),
            new FishmanFlanzMelandPaquin(), new FoglerRubinoff(), new Galicia(), new Goodmans(), new GrahamThompson(),
            new Harris(), new HeadrickRizikAlvarezAndFernandez(), new HNA(), new IbanezParkman(), new JimenezPena(),
            new KanukaThuringer(), new KuriBrena(), new Langlois(), new Lavery(), new LawsonLundell(),
            new Leaf(), new LEGlobal(), new Lerners(), new LivingstonAlexanderAndLevy(), new LoopstraNixon(),
            new MalpicaIturbeBujParedes(), new MBM(), new McDougallGauley(), new McKercher(), new McKinneyBancroftAndHughes(),
            new MedinaGarnesAbogados(), new MGGL(), new MijaresAngoitiaCortesAndFuentes(), new MLTAikins(), new NaderHayauxAndGoebel(),
            new NautaDutilh(), new NelliganLaw(), new NunesScholefieldDeLeonAndCo(), new ONeillAndBorges(), new OslerHoskinAndHarcourt(),
            new OyenWiggs(), new Patterson(), new PattersonMairHamilton(), new PerezCorreaGonzalez(), new PietrantoniMendezAndAlvarezLLC(),
            new Pitblado(), new PrasadAndCompany(), new RamosRipollSchuster(), new RGRH(), new RitchMueller(),
            new RitchMuellerAndNicolau(), new RoblesMiaja(), new Sangra(), new SangraMollerLLP(), new SantamarinaAndSteta(),
            new SIERRALatam(), new SmartAndBiggar(), new StikemanElliott(), new ThompsonDorfmanSweatman(), new Uhthoff(),
            new UlisesCabrera(), new VazquezTerceroAndZepeda(), new VilaAbogados(), new VillarrealVGF(), new WildeboerDellelce(),

            // Central America
            new AlburquerqueAbogados(), new Alcogal(), new DeCampsVasquezVarela(), new DelcoLaw(), new FabregaMolinoMulino(),
            new GalindoAriasLopez(), new GarciaBodan(), new GuzmanAriza(), new IcazaGonzalezRuizAndAleman(), new LexAtlas(),
            new Lexincorp(), new Lovill(), new McConnellValdes(), new MorganAndMorgan(), new MyersFletcherAndGordon(),
            new Nassar(),

            // South America
            new Abe(), new AbeledoGottheil(), new AguayoEcclefieldAndMartinez(), new AlbagliZaliasnik(), new AlessandriLawyers(),
            new AllendeAndBrea(), new AlvarezAbogados(), new Amprimo(), new Andersen(), new Aninat(),
            new BaleraBerbelMitne(), new BalmacedaCoxPina(), new BaptistaLuz(), new BAQSN(), new BaraonaMarshall(),
            new BarcellosTucunduva(), new BarriosAndFuentes(), new BarrosAndErrazuriz(), new BaschRameh(), new BBGS(),
            new BeccarVarela(), new BenitesVargasUgaz(), new Bermudes(), new BicalhoNavarro(), new BicharaEMotta(),
            new Bocater(), new BofillMir(), new Bomchil(), new Bragard(), new BrasilSalomaoeMatthes(),
            new BrigrardUrrutia(), new BRZ(), new BSVV(), new BullrichFlanzbaum(), new BustamanteFabara(),
            new CARAdvogados(), new Carey(), new CariolaDiezPerezCotapos(), new Cassagne(), new CBLM(),
            new CEPDAbogados(), new Chediak(), new ChevezRuizZamarripa(), new ChrupoEvans(), new ClaroYCia(),
            new CoronelPerez(), new CPB(), new CrialesUrcullo(), new CTPAdvogados(), new Damma(),
            new DiasDeSouza(), new DiBlasiParente(), new DuarteGarcia(), new ErnestoBorges(), new EsguerraJHR(),
            new EstudioOlaechea(), new FarrocoAbreuGuarnieriZotelli(), new Felsberg(), new FerradaNehme(), new Ferrere(),
            new FischerCia(), new FitzwilliamStone(), new FLH(), new FMDerraik(), new GaiaSilvaGaedeAndAssociados(),
            new GNBLaw(), new GranadeiroGuimaraes(), new GSALegal(), new GuerreroOlivos(), new GumucioAbogados(),
            new GuyerRegules(), new HDLegal(), new HernandezAndCia(), new HopeDugganSilva(), new IWMelcheds(),
            new JArmandoBatista(), new JDSellierAndCo(), new JustenPereira(), new KVLAW(), new LabbeAbogados(),
            new LacazMartinsPereiraNetoGurevichAndSchoueri(), new LatinAlliance(), new LavAbogadosConsultores(), new LEFOSSE(), new Lembeye(),
            new LexvalorAbogados(), new LoboDeRizzo(), new LRILaw(), new MachadoAssociados(), new MachadoMeyer(),
            new Madrona(), new MadrugaBTW(), new MattosEngelbergEchenique(), new MelloTorres(), new MENPA(),
            new MHRLegal(), new MirandaAmado(), new MitraniCaballero(), new MolinaRios(), new MontauryPimentaMachadoAndVieiraDeMello(),
            new MoralesYBesa(), new MorenoBaldivieso(), new MottaFernandes(), new MPATradeLaw(), new MQMGLD(),
            new MUC(), new MundieEAdvogados(), new MunizLaw(), new NelsonWiliansAndAdvogados(), new NFA(),
            new NHM(), new NoboaPenaTorres(), new NovotnyAdvogados(), new OFarrell(), new Olaechea(),
            new OlarteMoure(), new Osterling(), new PalaciosLleras(), new PalmaLaw(), new PayetReyCauviPerez(),
            new PeixotoCury(), new PGLaw(), new PHRLegal(), new PNMAdovagos(), new PPOAbogados(),
            new PradoVidigal(), new Prieto(), new PSTBN(), new PugaOrtiz(), new RennoPenteadoSampaioAdvogados(),
            new RiedFabres(), new RMADVAdvogados(), new RobortellaEPeres(), new RodrigoEliasMedrano(), new RodriguezAngobaldo(),
            new RomeuAmaralAdvogados(), new RosselloAbogados(), new RossiMaffiniMilmanGrando(), new Rubio(), new SachaCalmon(),
            new Santivanez(), new SantosBevilaquaAdvogados(), new SargentAndKrahn(), new SchneiderPugliese(), new SerranoMartinezCMA(),
            new Silva(), new SiqueiraCastro(), new Spingarn(), new TAGD(), new TauilAndChequer(),
            new Tavares(), new TraviesoEvansArriaAndRengel(), new TrenchRossiWatanabe(), new Tumnet(), new UrendaRencoretOrregoYDorr(),
            new VBDAdvogados(), new VBSOAdvogados(), new Velloza(), new VieiraRezendeAdvogados(), new Vouga(),
            new WaldAntunesVitaEBlattner(), new WilliamFreireAdvogados(), new WongtschowskiKleimanAdvogados(), new ZBV(), new ZuzunagaAssereto(),
    };

    private static final Site[] OCEANIA = {
            new AiGroup(), new AitkenPartners(), new AJLawAndCo(), new AndersonLloyd(), new AnthonyHarper(),
            new ArnoldBlochLeibler(), new BankiHaddockFiora(), new Baumgartners(), new BuddleFindlay(), new CarterNewell(),
            new Chamberlains(), new ClaytonUtz(), new ConnollySuthers(), new Corcoran(), new Corrs(),
            new DBHLaw(), new DeutschMiller(), new DoogueGeorge(), new DuncanCotterill(), new DWFoxTucker(),
            new Finlaysons(), new FpaPatent(), new Gadens(), new GilbertAndTobin(), new GordonLegal(),
            new GreenwoodRoche(), new Grette(), new GriffithHack(), new HamiltonLocke(), new HarmosHortonLusk(),
            new HeskethHenry(), new Hicksons(), new HoldingRedlich(), new HWEbsworth(), new JamesAndWells(),
            new JohnsonWinterSlattery(), new LaneNeave(), new MacphersonKelley(), new Madderns(), new MalleyAndCo(),
            new MatthewsFolbigg(), new MayneWetherell(), new MBIP(), new McCulloughRobertson(), new MellorOlsson(),
            new MinterEllisonRuddWatts(), new MorayAndAgnew(), new NormanWaterhouse(), new PiperAlderman(), new RussellMcVeagh(),
            new SierraLegal(), new SimmonsWolfhagen(), new SimpsonGrierson(), new ThomsonGeer(), new TompkinsWake(),
            new WengerVieliAG(), new WilsonHarle(), new WilsonRyanGrose(), new Wrays(), new WynnWilliams(),
            new YoungList(),
    };

    private static final Site[] MUNDIAL = {
            new ABAndDavid(), new AddleshawGoddardLLP(), new Adna(), new ALGoodbody(), new Allens(),
            new AlTamimi(), new ApplebyGlobal(), new ArnoldAndPorter(), new ArochiLindner(), new Ashurst(),
            new AVMAdvogados(), new AWA(), new BCLPLaw(), new BDO(), new Blakes(),
            new BNT(), new Bracewell(), new CareyOlsen(), new CassidyLevyKent(), new CerhaHempel(),
            new Chattertons(), new ClearyGottlieb(), new CliffordChance(), new Clyde(), new Cobalt(),
            new ControlRisks(), new Conyers(), new Cooley(), new CorporateINTL(), new CovingtonAndBurlingLLP(),
            new CrowellAndMoring(), new Cuatrecasas(), new Curtis(), new DCCLaw(), new DebevoiseAndPlimpton(),
            new Dentons(), new DSNavarroCastex(), new DuaneMorris(), new Ellex(), new EmploymentLawAlliance(),
            new EProint(), new FaegreDrinkerBiddleAndReath(), new FangdaPartners(), new FRA(), new Garrigues(),
            new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodwin(), new GreenbergTraurig(), new GuantaoLaw(),
            new HadefAndPartners(), new HakunLaw(), new HavelPartners(), new HaynesAndBoone(), new HBNLaw(),
            new HFW(), new HiggsAndJohnson(), new HillDickinson(), new HollandAndKnight(), new Houthoof(),
            new HuntonAndrewsKurth(), new JohnsonCamachoAndSingh(), new JonesDay(), new JPMAndPartners(), new JSKAdvokatni(),
            new KingAndWoodMallesons(), new Kinstellar(), new KISCHIP(), new KRySGlobal(), new LatamLex(),
            new LathamAndWatkins(), new Legance(), new LewissSilkin(), new LexCaribbean(), new Littler(),
            new LPALaw(), new Luther(), new MagnussonLaw(), new MarksAndClerk(), new MayerBrown(),
            new McCarthyTetrault(), new MdME(), new METIDA(), new Milbank(), new MillerThomsonLLP(),
            new MIOLaw(), new MorganLewis(), new Noerr(), new Ogier(), new Ogletree(),
            new OsborneClarke(), new PAGBAM(), new PaulHastings(), new PearlCohen(), new PeterAndKim(),
            new PeterkaAndPartners(), new PillsburyWinthropShawPittman(), new PortolanoCavallo(), new Pulegal(), new QuinEmanuel(),
            new ReinholdCohnGroup(), new RelianceCorporateAdvisors(), new RiadSalehAndPartners(), new Rimon(), new RopesAndGray(),
            new SabaAndCo(), new Schoenherr(), new SdzlegalSchindhelm(), new Secretariat(), new SheppardMullin(),
            new SimmonsAndSimmons(), new Skadden(), new Sorainen(), new SpencerWest(), new SprusonAndFerguson(),
            new SquirePattonBoggs(), new StephensonHarwood(), new StewartMcKelvey(), new SullivanAndCromwell(), new SullivanAndWorcester(),
            new TahotaLaw(), new TaylorWessing(), new TEMPLARS(), new Thommessen(), new Trowers(),
            new Vaneps(), new Walkers(), new WALLESS(), new WatsonFarleyAndWilliams(), new WhiteAndCase(),
            new WilliamFry(), new Willkie(), new WinstonAndStrawn(), new WithersKhattarWong(),
    };

    // Firms under testing/validation (sourced from core to_test module)
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
     * Builds the list of all firms based on enabled continents.
     * Mundial firms are always included (global firms).
     *
     * @return Array of Site objects for enabled continents + Mundial
     */
    public static Site[] build() {
        List<Site> sites = new ArrayList<>();

        if (ContinentConfig.isContinentEnabled("Africa"))   sites.addAll(Arrays.asList(AFRICA));
        if (ContinentConfig.isContinentEnabled("Asia"))     sites.addAll(Arrays.asList(ASIA));
        if (ContinentConfig.isContinentEnabled("Europe"))   sites.addAll(Arrays.asList(EUROPE));
        if (ContinentConfig.isContinentEnabled("Americas")) sites.addAll(Arrays.asList(AMERICAS));
        if (ContinentConfig.isContinentEnabled("Oceania"))  sites.addAll(Arrays.asList(OCEANIA));

        // Mundial is always included (global firms)
        sites.addAll(Arrays.asList(MUNDIAL));

        return sites.toArray(new Site[0]);
    }
}
