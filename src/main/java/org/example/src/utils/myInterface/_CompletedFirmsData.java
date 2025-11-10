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
            new Aera(), new AGPAdvokater(), new Alliotts(), new AlukoAndOyebode(), new Amorys(),
            new ApplebyGlobal(), new AraozAndRueda(), new ArnesenIP(), new AronTadmorLevy(), new Ashitiva(),
            new AWA(), new BAHR(), new BancilaDiaconuSiAsociatii(), new BankiHaddockFiora(), new BARDEHLEPAGENBERG(),
            new BARENTSKRANS(), new BarristonLaw(), new BennettJones(), new BLGLaw(), new BlakeMorgan(),
            new Blakes(), new BlandyAndBlandy(), new BNT(), new BonelliErede(), new BoodleHatfield(),
            new BrinkmannAndPartner(), new Broseta(), new BullAndCo(), new BurnetDuckworthAndPalmer(), new BussMurtonLaw(),
            new ByrneWallace(), new BYRO(), new CampbellsLegal(), new CareyOlsen(), new Cassels(),
            new CassidyLevyKent(), new CastrenAndSnellman(), new CBA(), new Chattertons(), new Cirio(),
            new Clarkslegal(), new ClarkWilson(), new ClemensLaw(), new CLPLaw(), new Codex(),
            new CollasCrill(), new Conyers(), new CWAAssociates(), new DahlLaw(), new DaleAndLessmann(),
            new Delcade(), new DillonEustace(), new DimitrovPetrovAndCo(), new DittmarAndIndrenius(), new Dompatent(),
            new DrzewieckiTomaszek(), new DZPLaw(), new ECLegalRubio(), new FaegreDrinkerBiddleAndReath(), new Fidal(),
            new FilipAndCompany(), new FillmoreRiley(), new Finreg360(), new FisherQuarmbyAndPfeifer(), new FIVERS(),
            new FluegelPreissner(), new FoglerRubinoff(), new Foyen(), new GanadoAdvocates(), new GarciaBodan(),
            new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodmans(), new Haavind(), new HabrakenRutten(),
            new HammarskioldAndCo(), new HannesSnellman(), new HavelPartners(), new Hayes(), new HaynesAndBoone(),
            new HBNLaw(), new HFAndCo(), new HFW(), new HillDickinson(), new HjulmandCaptain(),
            new HNA(), new HollandAndKnight(), new Holst(), new HPPAttorneys(), new Hugel(),
            new HuntonAndrewsKurth(), new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new Kanter(),
            new KantorAndImmerman(), new KewLaw(), new KienhuisLegal(), new Kinstellar(), new KISCHIP(),
            new KnezovicAndAssociates(), new KochanskiAndPartners(), new Kolster(), new Krogerus(), new Langlois(),
            new LaszczukAndWspolnicy(), new LawsonLundell(), new LEFOSSE(), new LEGlobal(), new LemstraVanDerKorst(),
            new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Logos(), new LoopstraNixon(),
            new LPAGGV(), new LPALaw(), new Lydian(), new MagnussonLaw(), new MAQS(),
            new Matheson(), new MazantiAndersen(), new MccannFitzGerald(), new McCarthyTetrault(), new McConnellValdes(),
            new McDougallGauley(), new McKercher(), new McMillan(), new MeitarLaw(), new MijaresAngoitiaCortesAndFuentes(),
            new MLTAikins(), new MoalemWeitemeyer(), new Molinari(), new MorogluArseven(), new MSP(),
            new NelliganLaw(), new NelsonWiliansAndAdvogados(), new NielsenNorager(), new Njord(), new Norens(),
            new NovaLaw(), new NPPLegal(), new NysinghAdvocatenNotarissenNV(), new Odigo(), new Ogier(),
            new Ogletree(), new OslerHoskinAndHarcourt(), new OyenWiggs(), new Paksoy(), new PanettaConsultingGroup(),
            new Pedersoli(), new Penta(), new PeterAndKim(), new PillsburyWinthropShawPittman(), new Ploum(),
            new RBK(), new RDJ(), new RitchMueller(), new RitchMuellerAndNicolau(), new RobortellaEPeres(),
            new RonanDalyJermyn(), new Roschier(), new RPCLegal(), new SantamarinaAndSteta(), new SchindlerAttorneys(),
            new Schoenherr(), new SchurtiPartners(), new Secretariat(), new Selmer(), new ShahidLaw(),
            new SheppardMullin(), new SHorowitzAndCo(), new SIRIUS(), new SmartAndBiggar(), new SpencerWest(),
            new STBB(), new SteinmetzHaringGurman(), new StewartMcKelvey(), new Tavares(), new TheartMey(),
            new ThomasBodstrom(), new Titov(), new TucaZbarcea(), new VanDerPutt(), new VBAdvocates(),
            new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(), new Walkers(), new WatsonFarleyAndWilliams(),
            new WildeboerDellelce(), new WilliamFry(), new WinstonAndStrawn(), new WolfTheiss(), new ZamfirescuRacotiPredoiu(),
            new MayerBrown(), new Orrick(), new Sidley(), new SquirePattonBoggs(), new SullivanAndWorcester(),
            new AVMAdvogados(), new FRA(), new SuarezDeVivero(), new PFPLaw(), new PeterkaAndPartners(),
            new ABAndDavid(), new BentsiEnchillLetsaAndAnkomah(), new FlichyGrange(),
            new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(), new PricaAndPartners(), new RadulescuAndMusoi(),
            new Elverdam(), new FironLaw(), new MerilampiAttorneys(), new MitelAndAsociatii(), new SelihAndPartnerji(),
            new BCLPLaw(), new ClearyGottlieb(), new DuaneMorris(), new NunzianteMagrone(),

    };

    public final static Site[] byNewPage = {
            /* Firms to avoid
            new ALGoodbody(), new ArthurCox(), new Dentons(), new MishconKaras(), new OsborneClarke(),
            */
            new ACAndR(), new AOil(), new AsafoAndCo(), new Astrea(), new BarneaAndCo(),
            new BDO(), new BCFLaw(), new Beauchamps(), new Belgravia(), new Borenius(),
            new BurgesSalmon(), new BWBLLP(), new CarneluttiLaw(), new Cobalt(), new ControlRisks(),
            new CRCCD(), new Cuatrecasas(), new DANUBIAPatentAndLaw(), new DGKV(), new Dottir(),
            new EBN(), new Ekelmans(), new Ellex(), new EllisonsSolicitors(), new ENSAfrica(),
            new EPAndC(), new Esche(), new FCMLimited(), new Ferrere(), new Fischer(),
            new FPSLaw(), new Frontier(), new Fylgia(), new GittiAndPartners(), new GORG(),
            new GornitzkyAndCo(), new Hamso(), new HamsoPatentybra(), new HansOffiaAndAssociates(), new HaslingerNagele(),
            new HiggsAndJohnson(), new Holmes(), new Horten(), new Houthoof(), new IbanezParkman(),
            new JacksonEttiAndEdu(), new JWP(), new KennedyVanderLaan(), new KeystoneLaw(), new Kondrat(),
            new KRBLaw(), new KuriBrena(), new Kvale(), new LambadariosLaw(), new LangsethAdvokat(),
            new LatamLex(), new Legalis(), new Legance(), new LePooleBekema(), new LewissSilkin(),
            new LexCaribbean(), new Lindahl(), new MarksAndClerk(), new MBM(), new MdME(),
            new METIDA(), new MeyerKoring(), new MillerThomsonLLP(), new MorrisLaw(), new Mourant(),
            new MSBSolicitors(), new MyersFletcherAndGordon(), new NaderHayauxAndGoebel(), new Noerr(), new OgletreeDeakins(),
            new OneEssexCourt(), new Onsagers(), new Oxera(), new PaviaAndAnsaldo(), new PearlCohen(),
            new PhilippeAndPartners(), new PortaAndConsulentiAssociati(), new PortolanoCavallo(), new PrasadAndCompany(), new PrinzAndPartner(),
            new PrueferAndPartner(), new QuinEmanuel(), new ReinhardSkuhraWeiseAndPartnerGbR(), new RocaJunyent(), new Sangra(),
            new SargentAndKrahn(), new SBGK(), new Sherrards(), new Sorainen(), new SZA(),
            new TEMPLARS(), new Thommessen(), new ThompsonDorfmanSweatman(), new Vaneps(), new Vischer(),
            new WardynskiAndPartners(), new Werksmans(), new Wiersholm(), new WikborgRein(), new ZeposAndYannopoulos(),
            new Shalakany(), new ZakiHashemAndPartners(), new ZulficarAndPartners(), new Curtis(), new Goerg(),
            new PMP(),
            new CerhaHempel(), new DKGV(), new KambourovAndPartners(), new WALLESS(),
            new JadekAndPensa(), new JPMAndPartners(), new NESTOR(), new RymarzZdortMaruta(),
            new JBLaw(), new Oppenheim(), new SlaughterAndMay(), new Valfor(),
    };

    public final static Site[] byFilter = {};

    public final static Site[] byClick = {};
}