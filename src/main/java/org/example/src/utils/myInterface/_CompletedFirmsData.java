package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.byPage.*;

@Getter
public class _CompletedFirmsData {

    public final static Site[] byPage = {
            /* Firms to avoid
            new AddleshawGoddardLLP(), new Andersen(), new ArnoldAndPorter(), new Ashurst(), new CliffordChance(),
            new CovingtonAndBurlingLLP(), new CrowellAndMoring(), new DavisPolkAndWardwell(), new DebevoiseAndPlimpton(), new DechertLLP(),
            new GorrissenFederspiel(), new GreenbergTraurig(), new HerbertSmithFreehillsKramer(), new JonesDay(), new KromannReumert(),
            new LathamAndWatkins(), new Milbank(), new MorganLewis(), new NautaDutilh(), new ProskauerRose(),
            new RopesAndGray(), new Skadden(), new StephensonHarwood(), new Stibbe(), new TaylorWessing(),
            new WhiteAndCase(), new Willkie(),
            */

            // Africa
            new AlukoAndOyebode(), new Ashitiva(), new BentsiEnchillLetsaAndAnkomah(), new FisherQuarmbyAndPfeifer(), new KantorAndImmerman(),
            new STBB(), new ShahidLaw(), new TheartMey(),

            // Asia
//            new ALMTLegal(), new AnandAndAnand(), new CFNLaw(), new Deacons(), new FoxAndMandal(),
//            new Helmsman(), new HowseWilliams(), new MZMLegal(), new OldhamLiAndNie(),
//            new RamdasAndWong(), new SEUM(), new SFKSLaw(), new VeritasLegal(), new ZhongziLaw(),
//            new BaeKimAndLee(), new DRAndAJU(), new GrandwayLaw(), new K1Chamber(),
//            new LeeAndKo(), new MASLaw(), new ShinAndKim()

            new AronTadmorLevy(), new FironLaw(), new HFAndCo(), new KECOLegal(),
            new MeitarLaw(), new MorogluArseven(),
            new Paksoy(),
            new SHorowitzAndCo(), new SteinmetzHaringGurman(), new TiruchelvamAssociates(),

            // Europe
            new BARDEHLEPAGENBERG(), new CampbellsLegal(), new DechertLLP(),
            new AGPAdvokater(), new Alliotts(), new Amorys(), new AraozAndRueda(), new ArnesenIP(),
            new BAHR(), new BARENTSKRANS(), new BYRO(), new BancilaDiaconuSiAsociatii(), new Berggren(),
            new BlakeMorgan(), new BlandyAndBlandy(), new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(),
            new Broseta(), new BullAndCo(), new BussMurtonLaw(), new ByrneWallace(), new CBA(),
            new CLPLaw(), new CWAAssociates(), new CastrenAndSnellman(), new Cirio(), new Clarkslegal(),
            new ClemensLaw(), new Codex(), new CollasCrill(), new DZPLaw(), new DahlLaw(),
            new DavisPolkAndWardwell(), new Delcade(), new DimitrovPetrovAndCo(), new DittmarAndIndrenius(), new Dompatent(),
            new DrzewieckiTomaszek(), new Elverdam(), new Esche(), new FIVERS(), new Fidal(),
            new FilipAndCompany(), new Finreg360(), new FlichyGrange(), new FluegelPreissner(), new Foyen(),
            new FranklinLaw(), new GanadoAdvocates(), new GorrissenFederspiel(), new HPPAttorneys(), new Haavind(),
            new HabrakenRutten(), new HammarskioldAndCo(), new HannesSnellman(), new Hayes(), new HjulmandCaptain(),
            new Holst(), new Hugel(), new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(),
            new Kallan(), new Kanter(), new KewLaw(), new KienhuisLegal(), new KnezovicAndAssociates(),
            new KochanskiAndPartners(), new Kolster(), new Krogerus(), new KromannReumert(), new LEXIA(),
            new LEXLogmannsstofa(), new LPAGGV(), new LaszczukAndWspolnicy(), new LemstraVanDerKorst(), new Liedekerke(),
            new Logos(), new Lydian(), new MAQS(), new MSP(), new MVJMarkovicVukoticJovkovic(),
            new Matheson(), new MazantiAndersen(), new MccannFitzGerald(), new MellingVoitishkinAndPartners(), new MerilampiAttorneys(),
            new MitelAndAsociatii(), new MoalemWeitemeyer(), new Molinari(), new MooreLegalKovacs(), new NOEWE(),
            new NPPLegal(), new NielsenNorager(), new Norens(), new NovaLaw(), new NunzianteMagrone(),
            new NysinghAdvocatenNotarissenNV(), new Odigo(), new PFPLaw(), new PanettaConsultingGroup(), new Pedersoli(),
            new Penta(), new Ploum(), new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(), new PricaAndPartners(),
            new ProskauerRose(), new RBK(), new RPCLegal(), new RadulescuAndMusoi(), new RocaJunyent(),
            new RoedlAndPartner(), new Roschier(), new SIRIUS(), new SchindlerAttorneys(), new SchurtiPartners(),
            new SelihAndPartnerji(), new Selmer(), new Stibbe(), new SuarezDeVivero(), new ThomasBodstrom(),
            new Titov(), new TucaZbarcea(), new VBAdvocates(), new VanDerPutt(), new Vinge(),
            new WolfTheiss(), new ZamfirescuRacotiPredoiu(), new Aera(), new Njord(), new Orrick(),
            new Sidley(), new VOPatentsAndTrademarks(), new VieringJentschuraAndPartner(),
            new Hellstrom(), new Jalsovszky(), new Knijff(), new McDermottWillAndEmery(), new Eubelius(),
            new GrataInternational(), new GreenHorseLegal(), new Hellstrom(), new Jalsovszky(),
            new Knijff(), new McDermottWillAndEmery(), new PayetReyCauviPerez(),


            // North America
            new BLGLaw(), new BarristonLaw(), new BennettJones(), new BurnetDuckworthAndPalmer(), new Cassels(),
            new ClarkWilson(), new DaleAndLessmann(), new DeethWilliamsWall(), new DillonEustace(), new ECLegalRubio(),
            new FillmoreRiley(), new FoglerRubinoff(), new Goodmans(), new HNA(), new LEGlobal(),
            new Langlois(), new LawsonLundell(), new LoopstraNixon(), new MLTAikins(), new McDougallGauley(),
            new McKercher(), new MijaresAngoitiaCortesAndFuentes(), new NautaDutilh(), new NelliganLaw(), new OslerHoskinAndHarcourt(),
            new OyenWiggs(), new RitchMueller(), new RitchMuellerAndNicolau(), new SantamarinaAndSteta(), new SmartAndBiggar(),
            new StikemanElliott(), new WildeboerDellelce(),

            // Central America
            new GarciaBodan(), new McConnellValdes(),

            // South America
            new AguayoEcclefieldAndMartinez(), new Andersen(), new LEFOSSE(), new NelsonWiliansAndAdvogados(), new RennoPenteadoSampaioAdvogados(),
            new RobortellaEPeres(), new Tavares(), new PayetReyCauviPerez(), new PayetReyCauviPerez(),

            // Oceania
//            new BankiHaddockFiora(), new DBHLaw(), new DuncanCotterill(), new MBIP(), new MacphersonKelley(),
//            new MalleyAndCo(), new MinterEllisonRuddWatts(), new JamesAndWells(),

            // Mundial
            new AWA(), new AddleshawGoddardLLP(), new AlTamimi(), new Allens(), new ApplebyGlobal(),
            new Ashurst(), new BCLPLaw(), new CareyOlsen(), new ClearyGottlieb(), new CliffordChance(),
            new CovingtonAndBurlingLLP(), new CrowellAndMoring(), new DCCLaw(), new DebevoiseAndPlimpton(), new DuaneMorris(),
            new FRA(), new FaegreDrinkerBiddleAndReath(), new GianniAndOrigoni(), new GomezAceboAndPombo(), new GreenbergTraurig(),
            new HBNLaw(), new HFW(), new HaynesAndBoone(), new HerbertSmithFreehillsKramer(), new HillDickinson(),
            new HuntonAndrewsKurth(), new JonesDay(), new LathamAndWatkins(), new Milbank(), new MorganLewis(),
            new Ogier(), new PaulHastings(), new PeterAndKim(), new RopesAndGray(), new Schoenherr(),
            new Secretariat(), new SheppardMullin(), new Skadden(), new SpencerWest(), new SprusonAndFerguson(),
            new SquirePattonBoggs(), new StephensonHarwood(), new TahotaLaw(), new TaylorWessing(), new Walkers(),
            new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WilliamFry(), new Willkie(), new WinstonAndStrawn(),
            new ABAndDavid(), new AVMAdvogados(), new ArnoldAndPorter(), new BNT(),
            new Blakes(), new CassidyLevyKent(), new Chattertons(), new ConsortiumLegal(),
            new Conyers(), new Goodwin(), new HakunLaw(),
            new HavelPartners(), new HollandAndKnight(), new JSKAdvokatni(),
            new KISCHIP(), new Kinstellar(),
            new MagnussonLaw(), new MayerBrown(), new McCarthyTetrault(), new MillerThomsonLLP(),
            new Ogletree(), new PAGBAM(), new PeterkaAndPartners(), new PillsburyWinthropShawPittman(),
            new RelianceCorporateAdvisors(), new StewartMcKelvey(), new SullivanAndWorcester(),
    };

    public final static Site[] byNewPage = {
            /* Firms to avoid
            new ALGoodbody(), new ArthurCox(), new Dentons(), new MishconKaras(), new OsborneClarke(),
            */

            // Africa
            new ENSAfrica(), new HansOffiaAndAssociates(), new JacksonEttiAndEdu(), new Shalakany(), new Werksmans(),
            new ZakiHashemAndPartners(), new ZulficarAndPartners(),

            // Asia
//            new CovenantChambers(), new DSKLegal(), new HYLeungAndCo(), new HuiyeLaw(), new JSA(),
//            new LonganLaw(), new MishconKaras(), new Mourant(), new NurmansyahAndMuzdalifah(),
//            new RemfryAndSagar(), new SamvadPartners(), new TCLaw(), new TannerDeWitt(),

            new AOil(), new BarneaAndCo(), new EBN(), new Fischer(), new GornitzkyAndCo(), new KRBLaw(),


            // Europe
            new ABGIP(), new ACAndR(), new Astrea(), new BRAUNEISRECHTSANWALTE(), new Beauchamps(),
            new Belgravia(), new Borenius(), new BurgesSalmon(), new CRCCD(), new CarneluttiLaw(),
            new DANUBIAPatentAndLaw(), new DGKV(), new DKGV(), new Dottir(), new EPAndC(),
            new EisenfuhrSpeiserAndPartner(), new Ekelmans(), new EllisonsSolicitors(), new FCMLimited(), new FPSLaw(),
            new FrancisWilksAndJones(), new Frontier(), new Fylgia(), new GORG(), new GittiAndPartners(),
            new Goerg(), new GreeneAndGreeneSolicitors(), new Hamso(), new HarteBavendamm(), new HaslingerNagele(),
            new HoffmannEitle(), new Holmes(), new Horten(), new JBLaw(), new JWP(),
            new JadekAndPensa(), new KBVLLaw(), new KLCLaw(), new KWKRLaw(), new KambourovAndPartners(),
            new KennedyVanderLaan(), new KeystoneLaw(), new Kondrat(), new Kvale(), new LambadariosLaw(),
            new LangsethAdvokat(), new LePooleBekema(), new Legalis(), new Lindahl(), new MSBSolicitors(),
            new MaikowskiAndNinnemann(), new MeyerKoring(), new MorrisLaw(), new NESTOR(), new ONVLaw(),
            new OgletreeDeakins(), new OneEssexCourt(), new Onsagers(), new Oppenheim(), new Oxera(),
            new PMP(), new PhilippeAndPartners(), new PinneyTalfourdSolicitors(), new PortaAndConsulentiAssociati(), new Poulschmith(),
            new PrinzAndPartner(), new PrueferAndPartner(), new PuschWahlig(), new Racine(), new ReinhardSkuhraWeiseAndPartnerGbR(),
            new RonanDalyJermyn(), new RymarzZdortMaruta(), new SBGK(), new SKWSchwarz(), new SRSLegal(),
            new SZA(), new SampsonCowardLLP(), new Schoups(), new Sherrards(), new SlaughterAndMay(),
            new Strelia(), new Szecskay(), new Valfor(), new VanOlmenAndWynant(), new Vischer(),
            new VossiusAndPartner(), new WardynskiAndPartners(), new Wiersholm(), new WikborgRein(), new ZeposAndYannopoulos(),

            // North America
            new ArthurCox(), new AsafoAndCo(), new BCFLaw(), new BWBLLP(), new FilionWakelyThorupAngeletti(),
            new IbanezParkman(), new KuriBrena(), new MBM(), new NaderHayauxAndGoebel(), new PrasadAndCompany(),
            new Sangra(), new ThompsonDorfmanSweatman(), new VazquezTerceroAndZepeda(),

            // Central America
            new MyersFletcherAndGordon(),

            // South America
            new BrigrardUrrutia(), new Ferrere(), new LatinAlliance(), new Madrona(), new RMADVAdvogados(),
            new SargentAndKrahn(), new CariolaDiezPerezCotapos(),

            // Oceania
//            new Baumgartners(), new DWFoxTucker(), new DeutschMiller(), new Gadens(), new SimmonsWolfhagen(),
//            new TompkinsWake(), new YoungList(),

            // Mundial
            new ALGoodbody(), new ControlRisks(), new Cuatrecasas(), new Curtis(), new Dentons(),
            new EProint(), new GuantaoLaw(), new Houthoof(), new LewissSilkin(), new METIDA(),
            new MarksAndClerk(), new Noerr(), new OsborneClarke(), new PearlCohen(), new QuinEmanuel(),
            new SabaAndCo(), new Vaneps(),
            new BDO(), new CerhaHempel(), new Cobalt(), new Ellex(), new FangdaPartners(),
            new HiggsAndJohnson(), new JPMAndPartners(), new JohnsonCamachoAndSingh(), new LatamLex(), new Legance(),
            new LexCaribbean(), new MdME(), new PortolanoCavallo(), new Pulegal(), new SimmonsAndSimmons(),
            new Sorainen(), new TEMPLARS(), new Thommessen(), new WALLESS()
    };

    public final static Site[] byFilter = {};

    public final static Site[] byClick = {};
}